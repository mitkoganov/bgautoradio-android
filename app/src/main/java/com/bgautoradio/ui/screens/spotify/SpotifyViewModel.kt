package com.bgautoradio.ui.screens.spotify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.model.SpotifyPlaylist
import com.bgautoradio.data.model.SpotifyTrack
import com.bgautoradio.data.repository.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    private val repository: SpotifyRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _playlists  = MutableStateFlow<List<SpotifyPlaylist>>(emptyList())
    val playlists: StateFlow<List<SpotifyPlaylist>> = _playlists.asStateFlow()

    private val _isLoading  = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _authError  = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Playlist detail
    private val _openPlaylist  = MutableStateFlow<SpotifyPlaylist?>(null)
    val openPlaylist: StateFlow<SpotifyPlaylist?> = _openPlaylist.asStateFlow()

    private val _tracks        = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val tracks: StateFlow<List<SpotifyTrack>> = _tracks.asStateFlow()

    private val _tracksLoading = MutableStateFlow(false)
    val tracksLoading: StateFlow<Boolean> = _tracksLoading.asStateFlow()

    // Search
    private val _searchQuery   = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SpotifyTrack>>(emptyList())
    val searchResults: StateFlow<List<SpotifyTrack>> = _searchResults.asStateFlow()

    private val _isSearching   = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    init {
        checkLoginState()
        viewModelScope.launch {
            repository.authExpired.collect { expired ->
                if (expired) {
                    repository.clearAuthExpired()
                    logout()
                }
            }
        }
        viewModelScope.launch {
            repository.pendingCode.filterNotNull().collect { code ->
                repository.clearPendingCode()
                Log.d("SpotifyAuth", "Exchanging code for token")
                val ok = repository.exchangeCodeForToken(code)
                if (ok) {
                    _isLoggedIn.value = true
                    loadPlaylists()
                } else {
                    _authError.value = "Неуспешен вход — опитай пак"
                }
            }
        }
    }

    private fun checkLoginState() {
        viewModelScope.launch {
            val loggedIn = repository.isLoggedIn()
            _isLoggedIn.value = loggedIn
            if (loggedIn) loadPlaylists()
        }
    }

    fun buildAuthUrl() = repository.buildAuthUrl()

    private fun loadPlaylists() {
        viewModelScope.launch {
            _isLoading.value = true
            _playlists.value = repository.getUserPlaylists()
            _isLoading.value = false
        }
    }

    fun openPlaylist(playlist: SpotifyPlaylist) {
        _openPlaylist.value = playlist
        _tracks.value = emptyList()
        viewModelScope.launch {
            _tracksLoading.value = true
            _tracks.value = repository.getPlaylistTracks(playlist.id)
            _tracksLoading.value = false
        }
    }

    fun closePlaylist() {
        _openPlaylist.value = null
        _tracks.value = emptyList()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _isSearching.value = true
            _searchResults.value = repository.searchTracks(query)
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value   = ""
        _searchResults.value = emptyList()
        _isSearching.value   = false
    }

    fun playPlaylist(playlist: SpotifyPlaylist) = repository.playPlaylist(playlist)
    fun playTrack(track: SpotifyTrack)          = repository.playTrack(track)
    fun playTrackInPlaylist(playlist: SpotifyPlaylist, index: Int) =
        repository.playTrackInPlaylist(playlist.uri, index)
    fun playTracksFromLiked(index: Int) =
        repository.playTracksFromLiked(_tracks.value, index)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value   = false
            _playlists.value    = emptyList()
            _openPlaylist.value = null
            clearSearch()
        }
    }
}
