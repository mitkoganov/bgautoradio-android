package com.bgautoradio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.ui.theme.*

@Composable
fun PlaybackControls(
    status:           PlaybackStatus,
    isFavorite:       Boolean,
    onPlayPause:      () -> Unit,
    onNext:           () -> Unit,
    onPrevious:       () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier:         Modifier = Modifier,
    showPrevNext:     Boolean  = true
) {
    Row(
        modifier              = modifier,
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Favorite toggle
        AutoIconButton(
            onClick = onToggleFavorite,
            size    = 48.dp
        ) {
            Icon(
                imageVector        = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Премахни от любими" else "Добави в любими",
                tint               = if (isFavorite) AccentAmber else TextSecondary,
                modifier           = Modifier.size(28.dp)
            )
        }

        if (showPrevNext) {
            // Previous
            AutoIconButton(onClick = onPrevious, size = 56.dp) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Предишна станция",
                    tint     = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Play / Pause — big glowing button
        PlayPauseButton(
            status   = status,
            onClick  = onPlayPause,
            size     = 80.dp
        )

        if (showPrevNext) {
            // Next
            AutoIconButton(onClick = onNext, size = 56.dp) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Следваща станция",
                    tint     = TextPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayPauseButton(
    status:  PlaybackStatus,
    onClick: () -> Unit,
    size:    Dp
) {
    val isLoading = status == PlaybackStatus.LOADING || status == PlaybackStatus.RECONNECTING

    val pulseScale by rememberInfiniteTransition(label = "playPulse").animateFloat(
        initialValue = 1f,
        targetValue  = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val glowBrush = Brush.radialGradient(
        colors = listOf(AccentCyan, AccentCyanDim)
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (status == PlaybackStatus.PLAYING) pulseScale else 1f)
            .clip(CircleShape)
            .background(glowBrush)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier  = Modifier.size(32.dp),
                color     = TextOnAccent,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = when (status) {
                    PlaybackStatus.PLAYING -> Icons.Default.Pause
                    else                  -> Icons.Default.PlayArrow
                },
                contentDescription = if (status == PlaybackStatus.PLAYING) "Пауза" else "Пусни",
                tint     = TextOnAccent,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

@Composable
private fun AutoIconButton(
    onClick:  () -> Unit,
    size:     Dp,
    content:  @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(CardSurface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
