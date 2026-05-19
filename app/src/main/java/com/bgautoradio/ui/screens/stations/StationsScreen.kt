package com.bgautoradio.ui.screens.stations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.ui.components.EqualizerBars
import com.bgautoradio.ui.components.StationLogo
import com.bgautoradio.ui.theme.*

@Composable
fun StationsScreen(
    showCategoryFilter: Boolean = false,
    viewModel: StationsViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val activeId = playbackState.station?.id
    val isPlaying = playbackState.status == PlaybackStatus.PLAYING

    LaunchedEffect(showCategoryFilter) {
        if (showCategoryFilter) viewModel.clearFilters()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title    = "Станции",
            subtitle = "${uiState.stations.size} български онлайн радио · сортирани по популярност"
        )

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else if (uiState.filteredStations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Няма намерени станции", color = TextTertiary, fontSize = 16.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.filteredStations, key = { it.id }) { station ->
                    StationCard(
                        station    = station,
                        active     = station.id == activeId,
                        playing    = isPlaying && station.id == activeId,
                        onSelect   = { viewModel.play(station) },
                        isFav      = station.isFavorite,
                        onToggleFav = { viewModel.toggleFavorite(station) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Bg1.copy(alpha = 0.85f))
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text       = title,
            color      = TextPrimary,
            fontSize   = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
        Text(
            text     = subtitle,
            color    = TextTertiary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun StationCard(
    station:     RadioStation,
    active:      Boolean,
    playing:     Boolean,
    onSelect:    () -> Unit,
    isFav:       Boolean,
    onToggleFav: () -> Unit,
    modifier:    Modifier = Modifier
) {
    val borderColor = if (active) Accent.copy(alpha = 0.6f) else Border
    val bgColor     = if (active) AccentGlow else Panel

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .drawBehind {
                drawRoundRect(
                    color       = borderColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                    style        = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onSelect
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cover
        StationLogo(station = station, size = 72.dp, cornerRadius = 10.dp)

        // Meta
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text       = station.name,
                color      = TextPrimary,
                fontSize   = 19.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = station.category.displayName,
                color    = TextSecondary,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text     = station.displayCity,
                color    = TextTertiary,
                fontSize = 14.sp
            )
        }

        // Right side
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (active && playing) {
                EqualizerBars(bars = 4, playing = true, color = Accent, height = 22.dp)
            } else {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Пусни",
                    tint     = if (active) Accent else TextTertiary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Icon(
                imageVector        = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Любима",
                tint               = if (isFav) Danger else TextDisabled,
                modifier           = Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = onToggleFav
                    )
            )
        }
    }
}
