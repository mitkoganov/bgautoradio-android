package com.bgautoradio.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.ui.screens.stations.ScreenHeader
import com.bgautoradio.ui.screens.stations.StationCard
import com.bgautoradio.ui.screens.stations.StationsViewModel
import com.bgautoradio.ui.theme.*

private val quickFilters = listOf("Рок", "Новини", "Народна", "Денс", "Хитове", "София")

@Composable
fun SearchScreen(viewModel: StationsViewModel = hiltViewModel()) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    val activeId  = playbackState.station?.id
    val isPlaying = playbackState.status == PlaybackStatus.PLAYING
    val query     = uiState.searchQuery

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title    = "Търсене",
            subtitle = "Търси по име, жанр, град или честота"
        )

        // Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Bg1)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Search, contentDescription = null,
                tint = Accent, modifier = Modifier.size(22.dp))

            BasicTextField(
                value       = query,
                onValueChange = { viewModel.setSearch(it) },
                modifier    = Modifier.weight(1f),
                singleLine  = true,
                textStyle   = TextStyle(color = TextPrimary, fontSize = 16.sp),
                cursorBrush = SolidColor(Accent),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            text      = "напр. „рок“, „новини“...",
                            color     = TextDisabled,
                            fontSize  = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    inner()
                }
            )

            if (query.isNotEmpty()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Изчисти",
                    tint     = TextTertiary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null,
                            onClick           = { viewModel.setSearch("") }
                        )
                )
            }
        }

        // Quick filter chips (shown when no query)
        if (query.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Бързи филтри", color = TextTertiary, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickFilters.forEach { filter ->
                        QuickFilterChip(
                            label   = filter,
                            onClick = { viewModel.setSearch(filter.lowercase()) }
                        )
                    }
                }
            }
        }

        // Results
        if (query.isNotEmpty()) {
            if (uiState.filteredStations.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null,
                            tint = TextDisabled, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text     = "Няма съвпадения за '$query'.",
                            color    = TextSecondary,
                            fontSize = 15.sp
                        )
                    }
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
}

@Composable
private fun QuickFilterChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Panel)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
    }
}
