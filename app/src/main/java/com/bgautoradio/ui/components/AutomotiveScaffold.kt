package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.ui.theme.*

/**
 * Full-screen automotive scaffold.
 * Layout for 1920×720 / 1280×480 wide head-unit screens:
 *
 *  ┌──────────── TOP BAR ────────────────────────────────────────┐
 *  │  [App logo]  BULGARIAN AUTO RADIO    [Clock] [WiFi] [dots]  │
 *  ├─────────────────────────────────────────────────────────────┤
 *  │                    CONTENT AREA                             │
 *  │                (passed as [content])                        │
 *  └─────────────────────────────────────────────────────────────┘
 *  Bottom nav bar is rendered by AppNavigation's Scaffold.
 */
@Composable
fun AutomotiveScaffold(
    title:   String   = "BULGARIAN AUTO RADIO",
    isOnline: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceBlack)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(DarkNavy, DeepSpaceBlack, DarkNavy)
                    )
                )
                .padding(horizontal = 20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: App name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📻", fontSize = 18.sp)
                Text(
                    text      = title,
                    color     = TextPrimary,
                    fontSize  = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
            }

            // Right: Clock + network status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector        = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = if (isOnline) "Online" else "Offline",
                    tint               = if (isOnline) StatusGreen else StatusRed,
                    modifier           = Modifier.size(18.dp)
                )
                ClockWidget()
            }
        }

        // ── Content ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            content = content
        )
    }
}
