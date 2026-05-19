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
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bgautoradio.MainActivity

class FloatingRailService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: ComposeView? = null
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
        overlayView = null
        lifecycleOwner.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) stopSelf()
        return START_NOT_STICKY
    }

    companion object {
        const val ACTION_STOP = "STOP_OVERLAY"
        private const val NOTIF_ID = 9001
    }

    // ── Minimal LifecycleOwner for ComposeView in Service ────────────────────

    inner class ServiceLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
        private val registry = LifecycleRegistry(this)
        private val controller = SavedStateRegistryController.create(this)
        private val store = ViewModelStore()

        override val lifecycle: Lifecycle = registry
        override val viewModelStore: ViewModelStore = store
        override val savedStateRegistry: SavedStateRegistry = controller.savedStateRegistry

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

// ── Overlay UI ────────────────────────────────────────────────────────────────

private data class OverlayItem(val route: String, val icon: ImageVector)

private val overlayItems = listOf(
    OverlayItem("home",     Icons.Default.Radio),
    OverlayItem("channels", Icons.Default.LibraryMusic),
    OverlayItem("spotify",  Icons.Default.LibraryMusic),
    OverlayItem("apps",     Icons.Default.Apps),
)

@androidx.compose.runtime.Composable
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
