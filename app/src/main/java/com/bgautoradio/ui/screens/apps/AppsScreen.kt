package com.bgautoradio.ui.screens.apps

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.bgautoradio.ui.components.AppInfo
import com.bgautoradio.ui.overlay.FloatingRailService

@Composable
fun AppsScreen() {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val allApps = remember { loadLauncherApps(context) }
    val filtered = remember(query) {
        if (query.isBlank()) allApps
        else allApps.filter { it.appName.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value         = query,
            onValueChange = { query = it },
            placeholder   = { Text("Търси...", color = Color.White.copy(alpha = 0.4f)) },
            leadingIcon   = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.6f)) },
            singleLine    = true,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = Color(0xFF2A2A3E),
                unfocusedContainerColor = Color(0xFF1E1E30),
                focusedBorderColor      = Color(0xFF00BCD4),
                unfocusedBorderColor    = Color.White.copy(alpha = 0.15f),
                focusedTextColor        = Color.White,
                unfocusedTextColor      = Color.White,
            ),
            shape    = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(bottom = 8.dp),
        )

        LazyVerticalGrid(
            columns               = GridCells.Adaptive(minSize = 100.dp),
            contentPadding        = PaddingValues(4.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxSize(),
        ) {
            items(filtered, key = { it.packageName }) { app ->
                AppCell(app = app, onClick = { launchApp(context, app.packageName) })
            }
        }
    }
}

@Composable
private fun AppCell(app: AppInfo, onClick: () -> Unit) {
    val context = LocalContext.current
    val icon: Drawable? = remember(app.packageName) {
        runCatching { context.packageManager.getApplicationIcon(app.packageName) }.getOrNull()
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF22223A))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icon != null) {
            Image(
                bitmap             = icon.toBitmap(80, 80).asImageBitmap(),
                contentDescription = app.appName,
                modifier           = Modifier.size(48.dp),
            )
        } else {
            Spacer(Modifier.size(48.dp))
        }
        Text(
            text       = app.appName,
            color      = Color.White.copy(alpha = 0.9f),
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 2,
            overflow   = TextOverflow.Ellipsis,
            textAlign  = TextAlign.Center,
            lineHeight = 13.sp,
        )
    }
}

private fun launchApp(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

    // Start overlay rail if we have the permission, otherwise just launch normally
    if (Settings.canDrawOverlays(context)) {
        context.startService(Intent(context, FloatingRailService::class.java))
        // Small delay to let service create the overlay before we lose foreground
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            launchWithBounds(context, intent)
        }, 150)
    } else {
        // Ask for overlay permission
        val permIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(permIntent)
    }
}

private fun launchWithBounds(context: Context, intent: Intent) {
    runCatching {
        val options = ActivityOptions.makeBasic()
        // Overlay rail is 60dp; at 240dpi = 90px. App starts right after it.
        val screenWidth  = context.resources.displayMetrics.widthPixels
        val screenHeight = context.resources.displayMetrics.heightPixels
        val overlayPx    = (60 * context.resources.displayMetrics.density).toInt()
        options.launchBounds = Rect(overlayPx, 0, screenWidth, screenHeight)
        context.startActivity(intent, options.toBundle())
    }.onFailure {
        context.startActivity(intent)
    }
}

private fun loadLauncherApps(context: Context): List<AppInfo> {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .map { ri -> AppInfo(ri.activityInfo.packageName, ri.loadLabel(pm).toString()) }
        .sortedBy { it.appName.lowercase() }
        .distinctBy { it.packageName }
}
