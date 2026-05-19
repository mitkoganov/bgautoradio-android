package com.bgautoradio.playback

import android.content.ComponentName
import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bgautoradio.data.model.PlaybackState
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.data.preferences.AppPreferences
import com.bgautoradio.data.repository.RadioRepository
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PlaybackManager"

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: RadioRepository,
    private val prefs: AppPreferences
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    // Recently played history (newest first, capped at 20)
    private val _recentlyPlayed = MutableStateFlow<List<RadioStation>>(emptyList())
    val recentlyPlayed: StateFlow<List<RadioStation>> = _recentlyPlayed.asStateFlow()

    // Player reference set by the service via attachPlayer()
    private var player: Player? = null
    private var mediaController: MediaController? = null

    // Station queued before service was ready
    private var pendingStation: RadioStation? = null

    // Timestamp of last play() call — used to ignore the brief PAUSED that ExoPlayer
    // fires synchronously when switching streams (onIsPlayingChanged false fires ~50ms
    // after setMediaItem; Spotify audio-focus loss is seconds later by user action).
    private var lastPlayMs = 0L

    fun attachPlayer(p: Player) {
        player = p
        Log.d(TAG, "Player attached — executing pending station: ${pendingStation?.name}")
        // If a play was requested before service started, execute it now
        pendingStation?.let { station ->
            pendingStation = null
            playOnPlayer(station)
        }
    }

    fun detachPlayer() {
        player = null
    }

    // ── Connect UI to service via MediaController ─────────────────────────────

    fun connectToService() {
        if (mediaController != null) return   // already connected

        val sessionToken = SessionToken(
            context,
            ComponentName(context, RadioPlaybackService::class.java)
        )
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener({
            runCatching {
                mediaController = future.get()
                Log.d(TAG, "MediaController connected")
                // Execute pending station if present
                pendingStation?.let { station ->
                    pendingStation = null
                    playViaController(station)
                }
            }.onFailure { Log.e(TAG, "MediaController connect failed", it) }
        }, MoreExecutors.directExecutor())
    }

    fun disconnectFromService() {
        mediaController?.release()
        mediaController = null
    }

    // ── Playback commands ─────────────────────────────────────────────────────

    fun play(station: RadioStation) {
        Log.d(TAG, "play() called: ${station.name}, ctrl=$mediaController, player=$player")
        lastPlayMs = SystemClock.elapsedRealtime()

        _state.value = _state.value.copy(
            station      = station,
            status       = PlaybackStatus.LOADING,
            streamTitle  = null,
            errorMessage = null
        )
        scope.launch { prefs.setLastStationId(station.id) }

        // Track recently played (newest first, no duplicates, max 20)
        _recentlyPlayed.value = buildList {
            add(station)
            addAll(_recentlyPlayed.value.filter { it.id != station.id })
        }.take(20)

        when {
            mediaController != null -> playViaController(station)
            player != null          -> playOnPlayer(station)
            else -> {
                // Service not ready yet — queue and trigger connection
                Log.d(TAG, "Neither controller nor player ready — queuing ${station.name}")
                pendingStation = station
                connectToService()
            }
        }
    }

    private fun playViaController(station: RadioStation) {
        val item = buildMediaItem(station)
        mediaController!!.setMediaItem(item)
        mediaController!!.prepare()
        mediaController!!.play()
        Log.d(TAG, "playViaController: ${station.name} → ${station.streamUrl}")
    }

    private fun playOnPlayer(station: RadioStation) {
        val item = buildMediaItem(station)
        player!!.setMediaItem(item)
        player!!.prepare()
        player!!.play()
        Log.d(TAG, "playOnPlayer: ${station.name} → ${station.streamUrl}")
    }

    private fun buildMediaItem(station: RadioStation): MediaItem =
        MediaItem.Builder()
            .setUri(station.streamUrl)
            .setMediaId(station.id)
            .build()

    fun playPause() {
        val ctrl = mediaController
        if (ctrl != null) {
            if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
        } else {
            player?.let { p -> if (p.isPlaying) p.pause() else p.play() }
        }
    }

    fun stop() {
        mediaController?.stop() ?: player?.stop()
        _state.value = _state.value.copy(status = PlaybackStatus.IDLE)
    }

    // ── Station navigation ────────────────────────────────────────────────────

    fun playNext(navList: List<RadioStation>) {
        val current = _state.value.station ?: return
        val valid   = navList.filter { it.hasValidStream }
        if (valid.isEmpty()) return
        val idx  = valid.indexOfFirst { it.id == current.id }
        val next = if (idx >= 0) valid.getOrNull(idx + 1) ?: valid.first()
                   else          valid.first()
        play(next)
    }

    fun playPrevious(navList: List<RadioStation>) {
        val current = _state.value.station ?: return
        val valid   = navList.filter { it.hasValidStream }
        if (valid.isEmpty()) return
        val idx  = valid.indexOfFirst { it.id == current.id }
        val prev = if (idx >= 0) valid.getOrNull(idx - 1) ?: valid.last()
                   else          valid.last()
        play(prev)
    }

    // ── Status updates called by service ─────────────────────────────────────

    fun updateStatus(status: PlaybackStatus, error: String? = null) {
        // ExoPlayer fires onIsPlayingChanged(false) within ~50ms of setMediaItem() during a
        // station switch. Ignore that transient PAUSED for 400ms after play() so the Spotify
        // card does not flash. Audio-focus loss to Spotify is always triggered by user action
        // (seconds later), so it safely falls outside this window.
        if (status == PlaybackStatus.PAUSED &&
            SystemClock.elapsedRealtime() - lastPlayMs < 400L) return
        _state.value = _state.value.copy(status = status, errorMessage = error)
    }

    fun updateStreamTitle(title: String?) {
        if (!title.isNullOrBlank()) {
            _state.value = _state.value.copy(streamTitle = title)
        }
    }

    // ── Resume last station ───────────────────────────────────────────────────

    suspend fun getLastStation(): RadioStation? {
        val id = prefs.lastStationId.first() ?: return null
        return repository.getStation(id)
    }
}
