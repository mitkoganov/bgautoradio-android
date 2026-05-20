package com.bgautoradio.ui.screens.apps

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.bgautoradio.ui.theme.Accent
import com.bgautoradio.ui.theme.LocalAppColors

var RailEdgePx: Int = 0

@Composable
fun AppsScreen() {
    val context = LocalContext.current
    val isDark  = LocalAppColors.current.isDark
    val bg      = if (isDark) Color(0xFF0D0D1A) else Color(0xFFF0F4FF)
    val apps    = remember { loadApps(context) }

    LazyVerticalGrid(
        columns               = GridCells.Adaptive(minSize = 108.dp),
        contentPadding        = PaddingValues(12.dp),
        verticalArrangement   = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier              = Modifier.fillMaxSize().background(bg),
    ) {
        items(apps, key = { it.packageName }) { app ->
            AppTile(app = app, isDark = isDark, onClick = { launch(context, app.packageName) })
        }
    }
}

@Composable
private fun AppTile(app: AppInfo, isDark: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val icon: Drawable? = remember(app.packageName) {
        runCatching { context.packageManager.getApplicationIcon(app.packageName) }.getOrNull()
    }
    val cardBg    = if (isDark)
        Brush.verticalGradient(listOf(Color(0xFF1E1E32), Color(0xFF16162A)))
    else
        Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF0F4FF)))
    val textColor = if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF1A1A2E)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(
                0.5.dp,
                Brush.verticalGradient(listOf(Accent.copy(alpha = 0.18f), Color.Transparent)),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isDark) Color.White.copy(alpha = 0.06f)
                    else Color(0xFF1A1A2E).copy(alpha = 0.05f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Image(
                    bitmap             = icon.toBitmap(96, 96).asImageBitmap(),
                    contentDescription = app.appName,
                    modifier           = Modifier.size(44.dp),
                )
            } else {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(Accent.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = app.appName.take(1).uppercase(),
                        color      = Accent,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Text(
            text       = app.appName,
            color      = textColor,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 2,
            overflow   = TextOverflow.Ellipsis,
            textAlign  = TextAlign.Center,
            lineHeight = 14.sp,
        )
    }
}

private fun launch(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun loadApps(context: Context): List<AppInfo> {
    val pm     = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .map { ri -> AppInfo(ri.activityInfo.packageName, ri.loadLabel(pm).toString()) }
        .sortedBy { it.appName.lowercase() }
}
