package com.bgautoradio.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.ui.theme.*

data class RailItem(
    val route:   String,
    val icon:    ImageVector,
    val labelBg: String,
)

val railItems = listOf(
    RailItem("home",    Icons.Default.Radio,        "В ефир"),
    RailItem("channels",Icons.Default.LibraryMusic, "Станции"),
    RailItem("spotify", Icons.Default.LibraryMusic, "Spotify"),
    RailItem("apps",    Icons.Default.Apps,         "Приложения"),
)


@Composable
fun AutomotiveRail(
    currentRoute:      String,
    onNav:             (String) -> Unit,
    modifier:          Modifier        = Modifier,
    fontScale:         Float           = 1f,
    preset1:           AppInfo?        = null,
    preset2:           AppInfo?        = null,
    onPresetClick:     (Int) -> Unit   = {},
    onPresetLongClick: (Int) -> Unit   = {},
) {
    val activity = LocalContext.current as? android.app.Activity
    val isDark   = LocalAppColors.current.isDark
    val accentColor = LocalAppColors.current.accent

    Column(
        modifier = modifier
            .width(115.dp)
            .fillMaxHeight()
            .drawBehind {
                // Right edge — cyan glow line
                val x = size.width - 1.dp.toPx()
                drawLine(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            accentColor.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    start       = Offset(x, 0f),
                    end         = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Spacer(Modifier.height(72.dp))

        railItems.forEach { item ->
            RailNavItem(
                item      = item,
                active    = currentRoute == item.route,
                onClick   = { onNav(item.route) },
                fontScale = fontScale
            )
        }

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PresetButton(
    slot:        Int,
    station:     AppInfo?,
    fontScale:   Float,
    onClick:     () -> Unit,
    onLongClick: () -> Unit,
) {
    val isDark      = LocalAppColors.current.isDark
    val accentColor = LocalAppColors.current.accent
    val hasStation  = station != null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick     = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasStation) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppIcon(packageName = station!!.packageName, size = 32, tintWhite = true)
                Text(
                    text      = station.appName,
                    color     = if (isDark) TextTertiary else TextSecondary,
                    fontSize  = (12 * fontScale).sp,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.BookmarkAdd,
                    contentDescription = "Пресет $slot",
                    tint               = if (isDark) TextTertiary else TextSecondary,
                    modifier           = Modifier.size(22.dp)
                )
                Text(
                    text       = "Пресет $slot",
                    color      = if (isDark) TextTertiary else TextSecondary,
                    fontSize   = (12 * fontScale).sp,
                    maxLines   = 1,
                )
            }
        }
    }
}

@Composable
private fun RailNavItem(
    item:      RailItem,
    active:    Boolean,
    onClick:   () -> Unit,
    fontScale: Float = 1f
) {
    val isDarkRail   = LocalAppColors.current.isDark
    val inactiveColor = if (isDarkRail) TextTertiary else TextSecondary
    val textColor = if (active) Accent else inactiveColor
    val iconTint  = if (active) Accent else inactiveColor
    val iconSize  = if (active) 32.dp else 26.dp
    val activeBg  = if (LocalAppColors.current.isDark) SpecActiveCyanOverlay else AccentGlow25

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .height(72.dp)
            .then(
                if (active) Modifier.neonGlowBorder(
                    color        = Accent,
                    cornerRadius = 12.dp,
                    glowRadius   = 16f,
                    borderWidth  = 1.2.dp
                ) else Modifier
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) activeBg else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector        = item.icon,
                contentDescription = item.labelBg,
                tint               = iconTint,
                modifier           = Modifier.size(iconSize)
            )
            Text(
                text       = item.labelBg,
                color      = textColor,
                fontSize   = (12 * fontScale).sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                maxLines   = 1
            )
        }
    }
}
