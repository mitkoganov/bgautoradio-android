package com.bgautoradio.ui.screens.apps

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
    var query by remember { mutableStateOf("") }
    val allApps = remember { loadLauncherApps(context) }
    val filtered = remember(query) {
        if (query.isBlank()) allApps
        else allApps.filter { it.appName.contains(query, ignoreCase = true) }
    }
    val isDark = LocalAppColors.current.isDark
    val bg = if (isDark) Color(0xFF0D0D1A) else Color(0xFFF0F4FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
    ) {
        SearchBar(query = query, onChange = { query = it }, isDark = isDark)
        Spacer(Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Няма приложения", color = Color.White.copy(alpha = 0.4f), fontSize = 15.sp)
            }
        } else {
            LazyVerticalGrid(
                columns               = GridCells.Adaptive(minSize = 108.dp),
                contentPadding        = PaddingValues(4.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.fillMaxSize(),
            ) {
                items(filtered, key = { it.packageName }) { app ->
                    AppCell(
                        app    = app,
                        isDark = isDark,
                        onClick = { launchInFreeform(context, app.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onChange: (String) -> Unit, isDark: Boolean) {
    val containerColor = if (isDark) Color(0xFF1C1C2E) else Color(0xFFFFFFFF)
    val textColor      = if (isDark) Color.White else Color(0xFF1A1A2E)
    val hintColor      = textColor.copy(alpha = 0.35f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, null, tint = Accent.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) Text("Търси приложение…", color = hintColor, fontSize = 14.sp)
            BasicTextField(
                value         = query,
                onValueChange = onChange,
                singleLine    = true,
                textStyle     = TextStyle(color = textColor, fontSize = 14.sp),
                cursorBrush   = SolidColor(Accent),
                modifier      = Modifier.fillMaxWidth(),
            )
        }
        AnimatedVisibility(visible = query.isNotEmpty()) {
            IconButton(onClick = { onChange("") }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, null, tint = hintColor, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun AppCell(app: AppInfo, isDark: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val icon: Drawable? = remember(app.packageName) {
        runCatching { context.packageManager.getApplicationIcon(app.packageName) }.getOrNull()
    }
    val cardBg = if (isDark)
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
                .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFF1A1A2E).copy(alpha = 0.05f)),
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
                    Text(app.appName.take(1).uppercase(), color = Accent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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

@SuppressLint("BlockedPrivateApi")
private fun launchInFreeform(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

    val dm      = context.resources.displayMetrics
    val screenW = dm.widthPixels
    val screenH = dm.heightPixels
    val leftPx  = if (RailEdgePx > 0) RailEdgePx else (115 * dm.density).toInt()
    val topOffset = -(80 * dm.density).toInt()

    runCatching {
        val options = ActivityOptions.makeBasic()
        ActivityOptions::class.java
            .getDeclaredMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType)
            .also { it.isAccessible = true }
            .invoke(options, 5)
        options.launchBounds = Rect(leftPx, topOffset, screenW, screenH)
        context.startActivity(intent, options.toBundle())
    }.onFailure {
        context.startActivity(intent)
    }
}

private fun loadLauncherApps(context: Context): List<AppInfo> {
    val pm     = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return pm.queryIntentActivities(intent, 0)
        .map { ri -> AppInfo(ri.activityInfo.packageName, ri.loadLabel(pm).toString()) }
        .sortedBy { it.appName.lowercase() }
}
