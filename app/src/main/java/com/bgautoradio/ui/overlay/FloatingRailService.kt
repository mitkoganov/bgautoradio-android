package com.bgautoradio.ui.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bgautoradio.MainActivity
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.playback.PlaybackManager
import com.bgautoradio.ui.components.StationLogo
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FloatingRailService : Service() {

    @Inject lateinit var playbackManager: PlaybackManager

    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
    private var radioOverlayView: ComposeView? = null
    private val lifecycleOwner = ServiceLifecycleOwner()

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIF_ID, buildNotification())
        lifecycleOwner.start()
        showOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager?.removeViewImmediate(it) }
        radioOverlayView?.let { windowManager?.removeViewImmediate(it) }
        overlayView = null
        radioOverlayView = null
        lifecycleOwner.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP       -> stopSelf()
            ACTION_STOP_DRIVE -> stopDriveMode()
            else -> if (intent?.getBooleanExtra("drive_mode", false) == true) startDriveMode()
        }
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager = wm

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
            x = 0; y = 0
        }

        val view = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent { OverlayRail(::navigateAndBringToFront) }
        }
        overlayView = view
        wm.addView(view, params)
    }

    private fun startDriveMode() {
        if (radioOverlayView != null) return
        val wm = windowManager ?: return
        val radioPx = (200 * resources.displayMetrics.density).toInt()

        val params = WindowManager.LayoutParams(
            radioPx,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.END or Gravity.TOP
            x = 0; y = 0
        }

        val pm = playbackManager
        val view = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent { RadioOverlayPanel(pm = pm, onStop = ::stopDriveMode) }
        }
        radioOverlayView = view
        wm.addView(view, params)
    }

    private fun stopDriveMode() {
        radioOverlayView?.let { windowManager?.removeViewImmediate(it) }
        radioOverlayView = null
    }

    private fun navigateAndBringToFront(route: String) {
        OverlayCommand.navigateTo.tryEmit(route)
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
        startActivity(intent)
    }

    private fun buildNotification(): Notification {
        val channelId = "floating_rail"
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(channelId) == null) {
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Floating Rail", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, FloatingRailService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("BG Auto Radio")
            .setContentText("Floating rail активен")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Затвори", stopIntent)
            .build()
    }

    companion object {
        const val ACTION_STOP       = "STOP_OVERLAY"
        const val ACTION_STOP_DRIVE = "STOP_DRIVE_MODE"
        private const val NOTIF_ID  = 9001
    }

    // ── Minimal LifecycleOwner for ComposeView in Service ────────────────────

    inner class ServiceLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
        private val registry   = LifecycleRegistry(this)
        private val controller = SavedStateRegistryController.create(this)
        private val store      = ViewModelStore()

        override val lifecycle:           Lifecycle           = registry
        override val viewModelStore:      ViewModelStore      = store
        override val savedStateRegistry:  SavedStateRegistry  = controller.savedStateRegistry

        fun start() {
            controller.performRestore(null)
            registry.currentState = Lifecycle.State.STARTED
        }

        fun stop() {
            registry.currentState = Lifecycle.State.DESTROYED
            store.clear()
        }
    }
}

// ── Nav overlay rail UI ───────────────────────────────────────────────────────

private data class OverlayItem(val route: String, val icon: ImageVector)

private val overlayItems = listOf(
    OverlayItem("home",     Icons.Default.Radio),
    OverlayItem("channels", Icons.Default.LibraryMusic),
    OverlayItem("spotify",  Icons.Default.LibraryMusic),
    OverlayItem("apps",     Icons.Default.Apps),
)

@Composable
private fun OverlayRail(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight()
            .background(Color(0xDD0D0D1A)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        overlayItems.forEach { item ->
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onNavigate(item.route) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.route,
                    tint               = Color(0xFF00BCD4),
                    modifier           = Modifier.size(26.dp),
                )
            }
        }
    }
}

// ── Drive Mode radio panel (right side, 200dp) ────────────────────────────────

@Composable
private fun RadioOverlayPanel(pm: PlaybackManager, onStop: () -> Unit) {
    val state          by pm.state.collectAsStateWithLifecycle()
    val recentlyPlayed by pm.recentlyPlayed.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(Color(0xDD0D0D1A))
            .padding(horizontal = 8.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally,
    ) {
        // Close button
        Box(Modifier.fillMaxWidth()) {
            IconButton(
                onClick  = onStop,
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close, null,
                    tint     = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Station logo
        state.station?.let { station ->
            StationLogo(station = station, size = 56.dp, cornerRadius = 8.dp)
        } ?: Box(
            Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1A2E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Radio, null, modifier = Modifier.size(28.dp), tint = Color(0xFF00BCD4))
        }

        Spacer(Modifier.height(6.dp))

        // Station name
        Text(
            text     = state.station?.name ?: "—",
            color    = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )

        // RDS / stream title
        state.streamTitle?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text     = title,
                color    = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(8.dp))

        // Playback controls
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
                onClick  = { pm.playPrevious(recentlyPlayed) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.SkipPrevious, null, modifier = Modifier.size(20.dp), tint = Color.White)
            }
            IconButton(
                onClick  = { pm.playPause() },
                modifier = Modifier.size(36.dp)
            ) {
                val icon = if (state.status == PlaybackStatus.PLAYING) Icons.Default.Pause
                           else Icons.Default.PlayArrow
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = Color(0xFF00BCD4))
            }
            IconButton(
                onClick  = { pm.playNext(recentlyPlayed) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.SkipNext, null, modifier = Modifier.size(20.dp), tint = Color.White)
            }
        }
    }
}
