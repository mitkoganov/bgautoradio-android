package com.bgautoradio.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.data.preferences.AppPreferences
import com.bgautoradio.data.repository.ExternalMediaRepository
import com.bgautoradio.data.repository.RadioRepository
import com.bgautoradio.data.repository.SpotifyRepository
import com.bgautoradio.playback.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Log
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val allStations:     List<RadioStation> = emptyList(),
    val favoriteStations: List<RadioStation> = emptyList(),
    val presets:         List<RadioStation> = emptyList(),
    val isLoading:       Boolean            = true,
    val isOnline:        Boolean            = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository:              RadioRepository,
    private val playbackManager:         PlaybackManager,
    private val prefs:                   AppPreferences,
    private val externalMediaRepository: ExternalMediaRepository,
    private val spotifyRepository:       SpotifyRepository,
) : ViewModel() {

    val playbackState   = playbackManager.state
    val recentlyPlayed  = playbackManager.recentlyPlayed
    val externalMedia   = externalMediaRepository.state

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeStations()
        initializeCatalog()
        connectPlayback()
        autoPlayLastStation()
    }

    private fun observeStations() {
        viewModelScope.launch {
            repository.observeAllStations()
                .collect { stations ->
                    _uiState.update { state ->
                        state.copy(
                            allStations      = stations,
                            favoriteStations = stations.filter { it.isFavorite && it.hasValidStream },
                            presets          = buildPresets(stations),
                            isLoading        = false
                        )
                    }
                }
        }
    }

    private fun buildPresets(stations: List<RadioStation>): List<RadioStation> {
        // Presets = favorites first, then sorted by sortOrder, max 8
        val favorites    = stations.filter { it.isFavorite && it.hasValidStream }
        val nonFavorites = stations.filter { !it.isFavorite && it.hasValidStream }
            .sortedBy { it.sortOrder }
        return (favorites + nonFavorites)
            .distinctBy { it.id }
            .take(8)
    }

    private fun initializeCatalog() {
        viewModelScope.launch {
            repository.initializeCatalog()
        }
    }

    private fun connectPlayback() {
        playbackManager.connectToService()
    }

    private fun autoPlayLastStation() {
        viewModelScope.launch {
            if (!prefs.autoPlayOnStart.first()) return@launch
            if (playbackState.value.station != null) return@launch
            // Wait for station catalog to finish loading
            val loaded      = _uiState.first { !it.isLoading }
            val favorites   = loaded.favoriteStations
            val lastStation = playbackManager.getLastStation()
            val toPlay = when {
                lastStation != null && favorites.any { it.id == lastStation.id } -> lastStation
                favorites.isNotEmpty() -> favorites.first()
                else -> null
            }
            toPlay?.let { playbackManager.play(it) }
        }
    }

    // ── Playback actions ──────────────────────────────────────────────────────

    fun play(station: RadioStation) {
        if (!station.hasValidStream) return
        playbackManager.play(station)
    }

    fun playPause() = playbackManager.playPause()

    fun playNext() {
        viewModelScope.launch {
            val list = repository.getNavListOnce()
            if (list.isNotEmpty()) playbackManager.playNext(list)
        }
    }

    fun playPrevious() {
        viewModelScope.launch {
            val list = repository.getNavListOnce()
            if (list.isNotEmpty()) playbackManager.playPrevious(list)
        }
    }

    fun externalPlayPause() {
        val media = externalMedia.value
        Log.d("HomeVM", "externalPlayPause: pkg=${media?.packageName} isPlaying=${media?.isPlaying}")
        if (media?.packageName == "com.spotify.music") {
            if (media.isPlaying) spotifyRepository.remotePause()
            else spotifyRepository.remoteResume()
        } else {
            externalMediaRepository.playPause()
        }
    }

    fun externalSkipNext() {
        if (externalMedia.value?.packageName == "com.spotify.music") spotifyRepository.remoteSkipNext()
        else externalMediaRepository.skipToNext()
    }

    fun externalSkipPrevious() {
        if (externalMedia.value?.packageName == "com.spotify.music") spotifyRepository.remoteSkipPrev()
        else externalMediaRepository.skipToPrevious()
    }

    fun toggleFavorite(station: RadioStation) {
        viewModelScope.launch {
            repository.toggleFavorite(station)
        }
    }

    // ── Remote refresh ────────────────────────────────────────────────────────

    fun refreshCatalog() {
        viewModelScope.launch {
            repository.refreshFromRemote()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.disconnectFromService()
    }
}
