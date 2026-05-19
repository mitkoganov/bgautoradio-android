package com.bgautoradio.data.repository

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.bgautoradio.service.WazeNotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class ExternalMediaState(
    val packageName: String,
    val appLabel:    String,
    val title:       String?,
    val artist:      String?,
    val albumArt:    Bitmap?,
    val isPlaying:   Boolean,
)

@Singleton
class ExternalMediaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val _state = MutableStateFlow<ExternalMediaState?>(null)
    val state: StateFlow<ExternalMediaState?> = _state.asStateFlow()

    private val controllers  = mutableListOf<MediaController>()
    private val callbackMap  = mutableMapOf<String, MediaController.Callback>()
    private var sessionListener: MediaSessionManager.OnActiveSessionsChangedListener? = null
    private val msm by lazy {
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    }
    private val nlsComponent = ComponentName(context, WazeNotificationService::class.java)

    fun startListening() {
        try {
            updateControllers(msm.getActiveSessions(nlsComponent))
            val listener = MediaSessionManager.OnActiveSessionsChangedListener { sessions ->
                updateControllers(sessions ?: emptyList())
            }
            msm.addOnActiveSessionsChangedListener(listener, nlsComponent)
            sessionListener = listener
        } catch (_: SecurityException) {
            // NLS permission not granted — silent fail
        }
    }

    fun stopListening() {
        sessionListener?.let { msm.removeOnActiveSessionsChangedListener(it) }
        sessionListener = null
        clearControllers()
    }

    private fun updateControllers(sessions: List<MediaController>) {
        clearControllers()
        sessions
            .filter { it.packageName != context.packageName }
            .forEach { mc ->
                val cb = makeCallback()
                mc.registerCallback(cb)
                controllers.add(mc)
                callbackMap[mc.packageName] = cb
            }
        emitBestActive()
    }

    private fun clearControllers() {
        controllers.forEach { mc ->
            callbackMap[mc.packageName]?.let { mc.unregisterCallback(it) }
        }
        controllers.clear()
        callbackMap.clear()
    }

    private fun makeCallback() = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) { emitBestActive() }
        override fun onMetadataChanged(metadata: MediaMetadata?)   { emitBestActive() }
        override fun onSessionDestroyed()                          { emitBestActive() }
    }

    fun playFromUri(uri: String): Boolean {
        val ctrl = controllers.firstOrNull { it.packageName == "com.spotify.music" }
            ?: activeController()
            ?: return false
        ctrl.transportControls.playFromMediaId(uri, null)
        return true
    }

    fun playPause() {
        val mc = activeController() ?: return
        if (mc.playbackState?.state == PlaybackState.STATE_PLAYING)
            mc.transportControls.pause()
        else
            mc.transportControls.play()
    }

    fun skipToNext()     { activeController()?.transportControls?.skipToNext() }
    fun skipToPrevious() { activeController()?.transportControls?.skipToPrevious() }

    private fun activeController() =
        controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: controllers.firstOrNull()

    private fun emitBestActive() {
        val active = controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PAUSED }
        _state.value = active?.let { buildState(it) }
    }

    private fun buildState(mc: MediaController): ExternalMediaState {
        val meta = mc.metadata
        val appLabel = try {
            context.packageManager
                .getApplicationLabel(
                    context.packageManager.getApplicationInfo(mc.packageName, 0)
                ).toString()
        } catch (_: Exception) { mc.packageName }

        return ExternalMediaState(
            packageName = mc.packageName,
            appLabel    = appLabel,
            title       = meta?.getString(MediaMetadata.METADATA_KEY_TITLE),
            artist      = meta?.getString(MediaMetadata.METADATA_KEY_ARTIST),
            albumArt    = meta?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART),
            isPlaying   = mc.playbackState?.state == PlaybackState.STATE_PLAYING,
        )
    }
}
