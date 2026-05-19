package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.ui.theme.*

data class RailItem(
    val route:   String,
    val icon:    ImageVector,
    val labelBg: String,
)

val railItems = listOf(
    RailItem("home",       Icons.Default.Radio,        "В ефир"),
    RailItem("channels",   Icons.Default.LibraryMusic,  "Станции"),
    RailItem("favorites",  Icons.Default.Favorite,      "Любими"),
    RailItem("navigation", Icons.Default.Navigation,    "Нави"),
    RailItem("launcher",   Icons.Default.Home,          "Начало"),
)

@Composable
fun AutomotiveRail(
    currentRoute: String,
    onNav:        (String) -> Unit,
    modifier:     Modifier = Modifier,
    fontScale:    Float    = 1f
) {
    val activity = LocalContext.current as? android.app.Activity

    Column(
        modifier = modifier
            .width(140.dp)
            .fillMaxHeight()
            .background(Bg1)
            .drawBehind {
                drawLine(
                    color       = Border.copy(alpha = 0.5f),
                    start       = Offset(size.width, 0f),
                    end         = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        railItems.forEach { item ->
            val click: () -> Unit = if (item.route == "launcher") {
                {
                    activity?.let { ctx ->
                        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                            addCategory(android.content.Intent.CATEGORY_HOME)
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        ctx.startActivity(intent)
                    }
                }
            } else {
                { onNav(item.route) }
            }
            RailNavItem(
                item      = item,
                active    = currentRoute == item.route,
                onClick   = click,
                fontScale = fontScale
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun RailNavItem(
    item:      RailItem,
    active:    Boolean,
    onClick:   () -> Unit,
    fontScale: Float = 1f
) {
    val textColor = if (active) Accent else TextTertiary
    val iconTint  = if (active) Accent else TextTertiary
    val iconSize  = if (active) 34.dp else 28.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .height(76.dp)
            .then(
                if (active) Modifier.neonGlowBorder(
                    color        = Accent,
                    cornerRadius = 12.dp,
                    glowRadius   = 18f,
                    borderWidth  = 1.2.dp
                ) else Modifier
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) AccentGlow25 else Color.Transparent)
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
                fontSize   = (13 * fontScale).sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                maxLines   = 1
            )
        }
    }
}
