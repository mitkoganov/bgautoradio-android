package com.bgautoradio.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.data.model.PlaybackStatus
import com.bgautoradio.ui.FontScaleViewModel
import com.bgautoradio.ui.components.WazeAlertBanner
import com.bgautoradio.ui.theme.*

@Composable
fun HomeScreen(
    viewModel:   HomeViewModel      = hiltViewModel(),
    fontScaleVm: FontScaleViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val carouselScale by fontScaleVm.carouselScale.collectAsStateWithLifecycle()
    val externalMedia by viewModel.externalMedia.collectAsStateWithLifecycle()

    val favorites    = uiState.favoriteStations
    val radioPlaying = playbackState.status == PlaybackStatus.PLAYING
    val radioActive  = playbackState.status == PlaybackStatus.PLAYING
                    || playbackState.status == PlaybackStatus.LOADING
                    || playbackState.status == PlaybackStatus.RECONNECTING
    val current      = playbackState.station
    val showExternal = externalMedia != null && !radioActive

    when {
        uiState.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        }
        favorites.isEmpty() && !showExternal -> {
            NoFavoritesPlaceholder(Modifier.fillMaxSize())
        }
        else -> {
            val currentIndex = current
                ?.let { s -> favorites.indexOfFirst { it.id == s.id }.takeIf { it >= 0 } }
                ?: 0

            Box(Modifier.fillMaxSize()) {
                AnimatedContent(targetState = showExternal, label = "mediaSwitch") { isExternal ->
                    if (isExternal && externalMedia != null) {
                        ExternalMediaCard(
                            state         = externalMedia!!,
                            fontScale     = carouselScale,
                            onPlayPause   = { viewModel.externalPlayPause() },
                            onSkipNext    = { viewModel.externalSkipNext() },
                            onSkipPrev    = { viewModel.externalSkipPrevious() },
                        )
                    } else if (favorites.isNotEmpty()) {
                        CarouselNowPlaying(
                            stations     = favorites,
                            currentIndex = currentIndex,
                            playing      = radioPlaying,
                            streamTitle  = playbackState.streamTitle,
                            fontScale    = carouselScale,
                            onPrev       = { viewModel.playPrevious() },
                            onNext       = { viewModel.playNext() },
                            onSelect     = { idx -> viewModel.play(favorites[idx]) }
                        )
                    }
                }
                WazeAlertBanner(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun NoFavoritesPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint     = TextDisabled,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text       = "Няма любими станции",
                color      = TextTertiary,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text     = "Отиди в Станции и маркирай любимите си с ♥",
                color    = TextDisabled,
                fontSize = 16.sp
            )
        }
    }
}
