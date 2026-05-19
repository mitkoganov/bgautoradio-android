package com.bgautoradio.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.core.app.ServiceCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.icy.IcyInfo
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.bgautoradio.MainActivity
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.data.repository.RadioRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val ROOT_ID       = "__ROOT__"
private const val STATIONS_ID   = "__STATIONS__"

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class RadioPlaybackService : MediaLibraryService() {

    @Inject lateinit var playbackManager: PlaybackManager
    @Inject lateinit var radioRepository: RadioRepository

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var reconnectJob: Job? = null
    private var currentStation: RadioStation? = null
    private var skipNextMetadataUpdate = false

    // Cache of all stations for AA item resolution
    @Volatile private var stationsCache: List<RadioStation> = emptyList()

    // Cache of favorite stations — used for car browsing queue and next/prev navigation
    @Volatile private var favoritesCache: List<RadioStation> = emptyList()

    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)
            .setAudioAttributes(audioAttributes, true)
            .build()

        player.addListener(PlayerListener())

        val sessionActivityIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaLibrarySession.Builder(this, player, LibrarySessionCallback())
            .setSessionActivity(sessionActivityIntent)
            .build()

        playbackManager.attachPlayer(player)

        // Pre-load all stations (for item resolution in onAddMediaItems / onGetItem)
        serviceScope.launch {
            radioRepository.observeAllStations().collect { list ->
                stationsCache = list
            }
        }

        // Pre-load favorites cache — car browsing and next/prev navigation use only favorites
        serviceScope.launch {
            radioRepository.observeFavorites().collect { list ->
                favoritesCache = list.filter { it.hasValidStream }
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        reconnectJob?.cancel()
        player.stop()
        player.clearMediaItems()
        playbackManager.updateStatus(com.bgautoradio.data.model.PlaybackStatus.IDLE)
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        playbackManager.detachPlayer()
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    // ── Called from PlaybackManager ───────────────────────────────────────────

    fun playStation(station: RadioStation) {
        currentStation = station
        reconnectJob?.cancel()

        playbackManager.updateStreamTitle(null)

        val mediaItem = MediaItem.Builder()
            .setUri(station.streamUrl)
            .setMediaId(station.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(station.name)
                    .setArtist(station.city ?: station.category.displayName)
                    .setAlbumTitle(station.name)
                    .setArtworkUri(station.logoUrl?.let { android.net.Uri.parse(it) })
                    .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
                    .build()
            )
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    // ── Player event listener ─────────────────────────────────────────────────

    private inner class PlayerListener : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> { }
                Player.STATE_BUFFERING -> {
                    playbackManager.updateStatus(com.bgautoradio.data.model.PlaybackStatus.LOADING)
                }
                Player.STATE_READY -> {
                    playbackManager.updateStatus(
                        if (player.playWhenReady)
                            com.bgautoradio.data.model.PlaybackStatus.PLAYING
                        else
                            com.bgautoradio.data.model.PlaybackStatus.PAUSED
                    )
                }
                Player.STATE_ENDED -> {
                    scheduleReconnect()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playbackManager.updateStatus(
                if (isPlaying) com.bgautoradio.data.model.PlaybackStatus.PLAYING
                else com.bgautoradio.data.model.PlaybackStatus.PAUSED
            )
        }

        override fun onPlayerError(error: PlaybackException) {
            playbackManager.updateStatus(com.bgautoradio.data.model.PlaybackStatus.ERROR, error.message)
            scheduleReconnect(delayMs = 5_000L)
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            if (skipNextMetadataUpdate) {
                skipNextMetadataUpdate = false
                return
            }
            val title = mediaMetadata.title?.toString()
            playbackManager.updateStreamTitle(title)
            if (!title.isNullOrBlank() && title.contains(" - ")) {
                updateNowPlayingMetadata(title)
            }
        }

        // Direct ICY metadata path — more reliable than onMediaMetadataChanged with MediaLibrarySession
        override fun onMetadata(metadata: Metadata) {
            for (i in 0 until metadata.length()) {
                val entry = metadata[i]
                if (entry is IcyInfo && !entry.title.isNullOrBlank()) {
                    val icyTitle = entry.title!!
                    playbackManager.updateStreamTitle(icyTitle)
                    updateNowPlayingMetadata(icyTitle)
                    return
                }
            }
        }
    }

    private fun updateNowPlayingMetadata(icyTitle: String) {
        if (player.currentMediaItem == null) return
        skipNextMetadataUpdate = true
        val parts  = icyTitle.split(" - ", limit = 2)
        val title  = if (parts.size == 2) parts[1].trim() else icyTitle.trim()
        val artist = if (parts.size == 2) parts[0].trim() else null

        val idx  = player.currentMediaItemIndex
        val item = player.currentMediaItem ?: return
        // buildUpon() preserves artworkUri (station logo)
        val updated = item.buildUpon()
            .setMediaMetadata(
                item.mediaMetadata.buildUpon()
                    .setTitle(title)
                    .setArtist(artist ?: item.mediaMetadata.artist)
                    .build()
            )
            .build()
        player.replaceMediaItem(idx, updated)
    }

    // ── Auto-reconnect ────────────────────────────────────────────────────────

    private fun scheduleReconnect(delayMs: Long = 3_000L) {
        reconnectJob?.cancel()
        reconnectJob = serviceScope.launch {
            playbackManager.updateStatus(com.bgautoradio.data.model.PlaybackStatus.RECONNECTING)
            delay(delayMs)
            currentStation?.let { playStation(it) }
        }
    }

    // ── Android Auto Media Library callback ──────────────────────────────────

    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult = MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS)
            // Remove built-in seek-to-next/prev so the car falls back to KeyEvent / onMediaButtonEvent
            // which routes through favoritesCache instead of ExoPlayer's full queue
            .setAvailablePlayerCommands(
                androidx.media3.common.Player.Commands.Builder()
                    .addAllCommands()
                    .remove(androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    .remove(androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    .remove(androidx.media3.common.Player.COMMAND_SEEK_TO_NEXT)
                    .remove(androidx.media3.common.Player.COMMAND_SEEK_TO_PREVIOUS)
                    .build()
            )
            .build()

        override fun onMediaButtonEvent(
            session: MediaSession,
            controllerInfo: MediaSession.ControllerInfo,
            intent: Intent
        ): Boolean {
            @Suppress("DEPRECATION")
            val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return false
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return false
            when (keyEvent.keyCode) {
                KeyEvent.KEYCODE_MEDIA_NEXT,
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
                KeyEvent.KEYCODE_MEDIA_SKIP_FORWARD -> {
                    serviceScope.launch {
                        playbackManager.playNext(radioRepository.getNavListOnce())
                    }
                    return true
                }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS,
                KeyEvent.KEYCODE_MEDIA_REWIND,
                KeyEvent.KEYCODE_MEDIA_SKIP_BACKWARD -> {
                    serviceScope.launch {
                        playbackManager.playPrevious(radioRepository.getNavListOnce())
                    }
                    return true
                }
            }
            return false
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: MediaLibraryService.LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val root = MediaItem.Builder()
                .setMediaId(ROOT_ID)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setIsPlayable(false)
                        .setIsBrowsable(true)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                        .build()
                )
                .build()
            return Futures.immediateFuture(LibraryResult.ofItem(root, params))
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: MediaLibraryService.LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            if (parentId != ROOT_ID && parentId != STATIONS_ID) {
                return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE))
            }
            val items = favoritesCache.map { it.toBrowsableMediaItem() }
            return Futures.immediateFuture(
                LibraryResult.ofItemList(ImmutableList.copyOf(items), params)
            )
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val station = stationsCache.firstOrNull { it.id == mediaId }
                ?: return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE))
            return Futures.immediateFuture(LibraryResult.ofItem(station.toBrowsableMediaItem(), null))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val future = SettableFuture.create<List<MediaItem>>()
            serviceScope.launch {
                val resolved = mediaItems.mapNotNull { item ->
                    stationsCache.firstOrNull { it.id == item.mediaId }?.toMediaItem()
                        ?: item.takeIf { it.localConfiguration?.uri != null }
                }
                future.set(resolved)
            }
            return future
        }
    }
}
