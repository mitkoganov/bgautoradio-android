package com.bgautoradio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableFloatStateOf
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.ui.theme.*

@Composable
fun SignalStatusIndicator(
    status: PlaybackStatus,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when (status) {
            PlaybackStatus.PLAYING -> {
                PlayingDots()
                Text("LIVE", color = StatusGreen, fontSize = 11.sp, letterSpacing = 1.sp)
            }
            PlaybackStatus.LOADING, PlaybackStatus.RECONNECTING -> {
                PulseDot(color = AccentAmber)
                Text(
                    if (status == PlaybackStatus.LOADING) "ЗАРЕЖДА" else "ПОВТОРЕН ОПИТ",
                    color = AccentAmber, fontSize = 11.sp, letterSpacing = 1.sp
                )
            }
            PlaybackStatus.ERROR -> {
                Icon(
                    Icons.Default.SignalWifiOff,
                    contentDescription = "Грешка",
                    tint   = StatusRed,
                    modifier = Modifier.size(16.dp)
                )
                Text("ГРЕШКА", color = StatusRed, fontSize = 11.sp, letterSpacing = 1.sp)
            }
            PlaybackStatus.PAUSED -> {
                PulseDot(color = TextSecondary, pulse = false)
                Text("ПАУЗА", color = TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
            }
            PlaybackStatus.IDLE -> {
                Icon(
                    Icons.Default.SignalWifi4Bar,
                    contentDescription = "Готово",
                    tint   = TextDisabled,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "playing")
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
        listOf(0, 150, 300).forEachIndexed { i, delay ->
            val height by infiniteTransition.animateFloat(
                initialValue = 4f,
                targetValue  = 14f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(delay)
                ),
                label = "bar$i"
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height.dp)
                    .clip(CircleShape)
                    .background(StatusGreen)
            )
        }
    }
}

@Composable
private fun PulseDot(color: Color, pulse: Boolean = true) {
    val alpha by if (pulse) {
        rememberInfiniteTransition(label = "pulse").animateFloat(
            initialValue = 0.3f,
            targetValue  = 1f,
            animationSpec = infiniteRepeatable(
                animation  = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot"
        )
    } else {
        remember { mutableFloatStateOf(0.6f) }
    }

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}
