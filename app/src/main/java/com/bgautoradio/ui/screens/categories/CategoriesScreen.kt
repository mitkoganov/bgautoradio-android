package com.bgautoradio.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.ui.screens.stations.ScreenHeader
import com.bgautoradio.ui.screens.stations.StationCard
import com.bgautoradio.ui.screens.stations.StationsViewModel
import com.bgautoradio.ui.theme.*

@Composable
fun CategoriesScreen(viewModel: StationsViewModel = hiltViewModel()) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    val activeId  = playbackState.station?.id
    val isPlaying = playbackState.status == PlaybackStatus.PLAYING

    // Derive categories from available stations
    val availableCategories = remember(uiState.stations) {
        uiState.stations.map { it.category }.distinct().sortedBy { it.displayName }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title    = "Жанрове",
            subtitle = "Филтрирай по тип съдържание"
        )

        // Category chip row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Bg1)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" chip
            CategoryChip(
                label    = "Всички",
                count    = uiState.stations.size,
                active   = uiState.selectedCategory == null,
                onClick  = { viewModel.setCategory(null) }
            )
            availableCategories.forEach { cat ->
                val count = uiState.stations.count { it.category == cat }
                CategoryChip(
                    label   = cat.displayName,
                    count   = count,
                    active  = uiState.selectedCategory == cat,
                    onClick = { viewModel.setCategory(cat) }
                )
            }
        }

        val filtered = if (uiState.selectedCategory == null)
            uiState.stations
        else
            uiState.stations.filter { it.category == uiState.selectedCategory }

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("В тази категория няма станции.", color = TextTertiary, fontSize = 15.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { station ->
                    StationCard(
                        station     = station,
                        active      = station.id == activeId,
                        playing     = isPlaying && station.id == activeId,
                        onSelect    = { viewModel.play(station) },
                        isFav       = station.isFavorite,
                        onToggleFav = { viewModel.toggleFavorite(station) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label:   String,
    count:   Int,
    active:  Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (active) AccentGlow else Panel)
            .then(
                if (active) Modifier
                else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text       = label,
            color      = if (active) Accent else TextSecondary,
            fontSize   = 13.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text     = "$count",
            color    = if (active) Accent.copy(alpha = 0.7f) else TextDisabled,
            fontSize = 11.sp
        )
    }
}
