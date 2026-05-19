package com.bgautoradio.ui.screens.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.data.repository.RadioRepository
import com.bgautoradio.playback.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StationsUiState(
    val stations:         List<RadioStation> = emptyList(),
    val filteredStations: List<RadioStation> = emptyList(),
    val searchQuery:      String             = "",
    val selectedCategory: RadioCategory?     = null,
    val cities:           List<String>       = emptyList(),
    val selectedCity:     String?            = null,
    val isLoading:        Boolean            = true,
    val showOnlyVerified: Boolean            = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class StationsViewModel @Inject constructor(
    private val repository:     RadioRepository,
    private val playbackManager: PlaybackManager
) : ViewModel() {

    val playbackState = playbackManager.state

    private val _uiState = MutableStateFlow(StationsUiState())
    val uiState: StateFlow<StationsUiState> = _uiState.asStateFlow()

    private val searchQuery      = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<RadioCategory?>(null)
    private val selectedCity     = MutableStateFlow<String?>(null)
    private val showOnlyVerified = MutableStateFlow(false)

    init {
        observeStations()
        observeCities()
    }

    private fun observeStations() {
        viewModelScope.launch {
            combine(
                repository.observeAllStations(),
                searchQuery.debounce(200),
                selectedCategory,
                selectedCity,
                showOnlyVerified
            ) { stations, query, category, city, verifiedOnly ->
                val filtered = stations.filter { station ->
                    val matchesQuery    = query.isBlank() ||
                        station.name.contains(query, ignoreCase = true) ||
                        station.city?.contains(query, ignoreCase = true) == true ||
                        station.tags.any { it.contains(query, ignoreCase = true) }
                    val matchesCategory = category == null || station.category == category
                    val matchesCity     = city == null || station.city == city
                    val matchesVerified = !verifiedOnly || station.isVerified
                    matchesQuery && matchesCategory && matchesCity && matchesVerified
                }
                StationsUiState(
                    stations         = stations,
                    filteredStations = filtered,
                    searchQuery      = query,
                    selectedCategory = category,
                    selectedCity     = city,
                    showOnlyVerified = verifiedOnly,
                    isLoading        = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun observeCities() {
        viewModelScope.launch {
            repository.observeCities().collect { cities ->
                _uiState.update { it.copy(cities = cities) }
            }
        }
    }

    // ── Filter actions ────────────────────────────────────────────────────────

    fun setSearch(query: String)              { searchQuery.value = query }
    fun setCategory(c: RadioCategory?)        { selectedCategory.value = c }
    fun setCity(city: String?)                { selectedCity.value = city }
    fun setShowOnlyVerified(v: Boolean)       { showOnlyVerified.value = v }
    fun clearFilters() {
        searchQuery.value = ""
        selectedCategory.value = null
        selectedCity.value = null
        showOnlyVerified.value = false
    }

    // ── Playback ──────────────────────────────────────────────────────────────

    fun play(station: RadioStation) {
        if (!station.hasValidStream) return
        playbackManager.play(station)
    }

    fun toggleFavorite(station: RadioStation) {
        viewModelScope.launch {
            repository.toggleFavorite(station)
        }
    }
}
