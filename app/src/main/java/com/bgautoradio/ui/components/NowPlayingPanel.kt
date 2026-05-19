package com.bgautoradio.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.model.PlaybackState
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.ui.theme.*

/**
 * Left panel on the home screen — station logo + name + controls.
 * Designed for 1920×720 head units: takes ~45% of horizontal space.
 */
@Composable
fun NowPlayingPanel(
    state:            PlaybackState,
    isFavorite:       Boolean,
    onPlayPause:      () -> Unit,
    onNext:           () -> Unit,
    onPrevious:       () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier:         Modifier = Modifier
) {
    val station = state.station

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkNavy, DeepSpaceBlack)
                )
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement   = Arrangement.SpaceBetween,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        // Top: logo + station info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (station != null) {
                StationLogo(
                    station      = station,
                    size         = 140.dp,
                    cornerRadius = 20.dp
                )
            } else {
                EmptyLogoPlaceholder()
            }

            Spacer(Modifier.height(4.dp))

            // Station name
            AnimatedContent(
                targetState  = station?.name ?: "Изберете станция",
                label        = "stationName",
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                }
            ) { name ->
                Text(
                    text      = name,
                    style     = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        fontSize   = 26.sp
                    ),
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Category + city
            if (station != null) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    CategoryBadge(label = "${station.category.emoji} ${station.category.displayName}")
                    if (!station.city.isNullOrBlank()) {
                        Text(
                            text  = " · ${station.city}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }
                }
            }

            // ICY stream title
            if (!state.streamTitle.isNullOrBlank()) {
                Text(
                    text     = "♫  ${state.streamTitle}",
                    style    = MaterialTheme.typography.bodySmall.copy(
                        color    = AccentCyan,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Error message
            if (state.status == PlaybackStatus.ERROR && state.errorMessage != null) {
                Text(
                    text  = "⚠ ${state.errorMessage}",
                    style = MaterialTheme.typography.bodySmall.copy(color = StatusRed),
                    maxLines = 2
                )
            }
        }

        // Bottom: controls
        PlaybackControls(
            status           = state.status,
            isFavorite       = isFavorite,
            onPlayPause      = onPlayPause,
            onNext           = onNext,
            onPrevious       = onPrevious,
            onToggleFavorite = onToggleFavorite,
            modifier         = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun EmptyLogoPlaceholder() {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface),
        contentAlignment = Alignment.Center
    ) {
        Text("📻", fontSize = 56.sp)
    }
}

@Composable
private fun CategoryBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AccentCyanGlow)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall.copy(color = AccentCyan),
        )
    }
}
