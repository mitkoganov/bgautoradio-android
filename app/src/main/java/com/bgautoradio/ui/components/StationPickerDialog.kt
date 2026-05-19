package com.bgautoradio.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import com.bgautoradio.ui.theme.*

@Composable
fun AppPickerDialog(
    slot:      Int,
    apps:      List<AppInfo>,
    onSelect:  (AppInfo) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1A1A2A))
                .padding(vertical = 8.dp)
        ) {
            Text(
                text       = "Избери приложение за Пресет $slot",
                color      = Accent,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
            HorizontalDivider(color = Color(0xFF333355))

            LazyColumn {
                items(apps, key = { it.packageName }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(app); onDismiss() }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppIcon(packageName = app.packageName, size = 36)
                        Text(
                            text     = app.appName,
                            color    = Color.White,
                            fontSize = 14.sp,
                            maxLines = 1,
                        )
                    }
                    HorizontalDivider(color = Color(0xFF222233))
                }
            }
        }
    }
}

@Composable
fun AppIcon(packageName: String, size: Int, tintWhite: Boolean = false) {
    val context = LocalContext.current
    val bitmap: ImageBitmap? = remember(packageName) {
        runCatching {
            context.packageManager.getApplicationIcon(packageName)
                .toBitmap(size * 2, size * 2)
                .asImageBitmap()
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap             = bitmap,
            contentDescription = packageName,
            modifier           = Modifier.size(size.dp),
            colorFilter        = if (tintWhite) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
        )
    } else {
        Box(modifier = Modifier.size(size.dp).background(Color(0xFF333355), RoundedCornerShape(6.dp)))
    }
}
