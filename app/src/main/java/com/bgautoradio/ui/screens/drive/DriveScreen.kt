package com.bgautoradio.ui.screens.drive

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.ui.overlay.FloatingRailService
import com.bgautoradio.ui.theme.Accent
import com.bgautoradio.ui.theme.TextPrimary
import com.bgautoradio.ui.theme.TextSecondary

@Composable
fun DriveScreen() {
    val context = LocalContext.current
    val wazeInstalled = remember {
        context.packageManager.getLaunchIntentForPackage("com.waze") != null
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.DirectionsCar, null,
                tint     = Accent,
                modifier = Modifier.size(64.dp)
            )
            Text(
                "Drive Mode",
                color      = TextPrimary,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Радиото се показва вдясно, Waze в средата",
                color     = TextSecondary,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center
            )
            if (wazeInstalled) {
                Button(
                    onClick  = { launchWazeDriveMode(context) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Default.DirectionsCar, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Стартирай с Waze", color = Color.White, fontSize = 16.sp)
                }
            } else {
                Text("Waze не е инсталиран", color = Color(0xFFFF5252), fontSize = 14.sp)
            }
        }
    }
}

@SuppressLint("BlockedPrivateApi")
fun launchWazeDriveMode(context: Context) {
    if (!Settings.canDrawOverlays(context)) return

    // Add the right-side radio overlay
    context.startService(
        Intent(context, FloatingRailService::class.java).putExtra("drive_mode", true)
    )

    // Launch Waze into the middle area after overlays are fully drawn
    Handler(Looper.getMainLooper()).postDelayed({
        val intent = context.packageManager
            .getLaunchIntentForPackage("com.waze") ?: return@postDelayed
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val density = context.resources.displayMetrics.density
        val screenW = context.resources.displayMetrics.widthPixels
        val screenH = context.resources.displayMetrics.heightPixels
        val leftPx  = (60  * density).toInt()
        val rightPx = (200 * density).toInt()

        val options = ActivityOptions.makeBasic()
        // Windowing mode FIRST, then bounds — order matters
        runCatching {
            ActivityOptions::class.java
                .getDeclaredMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType)
                .also { it.isAccessible = true }
                .invoke(options, 5) // WINDOWING_MODE_FREEFORM = 5
        }
        options.launchBounds = Rect(leftPx, 0, screenW - rightPx, screenH)
        context.startActivity(intent, options.toBundle())
    }, 500)
}
