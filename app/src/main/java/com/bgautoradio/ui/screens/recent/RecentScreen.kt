package com.bgautoradio.ui.screens.recent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.bgautoradio.ui.screens.home.HomeViewModel
import com.bgautoradio.ui.screens.stations.ScreenHeader
import com.bgautoradio.ui.theme.*

@Composable
fun RecentScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val playbackState  by viewModel.playbackState.collectAsStateWithLifecycle()
    val recentStations by viewModel.recentlyPlayed.collectAsStateWithLifecycle()

    val activeId  = playbackState.station?.id
    val isPlaying = playbackState.status == PlaybackStatus.PLAYING

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title    = "Скоро слушани",
            subtitle = "${recentStations.size} станции · история на текущата сесия"
        )

        if (recentStations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = null,
                        tint = TextDisabled, modifier = Modifier.size(56.dp))
                    Text("Все още нямаш история.", color = TextSecondary, fontSize = 15.sp)
                    Text("Пусни някоя станция и тя ще се появи тук.",
                        color = TextTertiary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(recentStations, key = { _, s -> s.id }) { index, station ->
                    RecentRow(
                        index    = index,
                        station  = station,
                        active   = station.id == activeId,
                        playing  = isPlaying && station.id == activeId,
                        onClick  = { viewModel.play(station) }
                    )
                    if (index < recentStations.lastIndex) {
                        HorizontalDivider(
                            modifier  = Modifier.padding(horizontal = 16.dp),
                            color     = Border,
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentRow(
    index:   Int,
    station: RadioStation,
    active:  Boolean,
    playing: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (active) AccentGlow else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Index number
        Text(
            text       = "${index + 1}",
            color      = TextDisabled,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.width(20.dp)
        )

        // Cover
        StationLogo(station = station, size = 52.dp, cornerRadius = 8.dp)

        // Meta
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text       = station.name,
                color      = if (active) TextPrimary else TextPrimary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = "${station.category.displayName} · ${station.displayCity}",
                color    = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1
            )
        }

        // Play indicator
        if (active && playing) {
            EqualizerBars(bars = 4, playing = true, color = Accent, height = 18.dp)
        } else {
            Icon(Icons.Default.PlayArrow, contentDescription = null,
                tint = if (active) Accent else TextTertiary,
                modifier = Modifier.size(22.dp))
        }
    }
}
