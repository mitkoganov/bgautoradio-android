package com.bgautoradio.ui.screens.spotify

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bgautoradio.data.model.SpotifyPlaylist
import com.bgautoradio.data.model.SpotifyTrack
import com.bgautoradio.ui.theme.*
import java.util.concurrent.TimeUnit

private val SpotifyGreen = Color(0xFF1DB954)

private sealed class SpotifyUiState {
    object Login          : SpotifyUiState()
    object Loading        : SpotifyUiState()
    object Playlists      : SpotifyUiState()
    object SearchResults  : SpotifyUiState()
    data class Detail(val playlist: SpotifyPlaylist) : SpotifyUiState()
}

@Composable
fun SpotifyScreen(
    viewModel: SpotifyViewModel = hiltViewModel(),
) {
    val isLoggedIn     by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val playlists      by viewModel.playlists.collectAsStateWithLifecycle()
    val isLoading      by viewModel.isLoading.collectAsStateWithLifecycle()
    val authError      by viewModel.authError.collectAsStateWithLifecycle()
    val openPlaylist   by viewModel.openPlaylist.collectAsStateWithLifecycle()
    val tracks         by viewModel.tracks.collectAsStateWithLifecycle()
    val tracksLoading  by viewModel.tracksLoading.collectAsStateWithLifecycle()
    val searchQuery    by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults  by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching    by viewModel.isSearching.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val uiState: SpotifyUiState = when {
        !isLoggedIn              -> SpotifyUiState.Login
        openPlaylist != null     -> SpotifyUiState.Detail(openPlaylist!!)
        isLoading                -> SpotifyUiState.Loading
        searchQuery.isNotBlank() -> SpotifyUiState.SearchResults
        else                     -> SpotifyUiState.Playlists
    }

    AnimatedContent(targetState = uiState, label = "spotifyState") { state ->
        when (state) {
            SpotifyUiState.Login -> LoginPrompt(
                authError = authError,
                onLogin = {
                    val url    = viewModel.buildAuthUrl()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
            )
            SpotifyUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SpotifyGreen)
            }
            is SpotifyUiState.Detail -> PlaylistDetail(
                playlist       = state.playlist,
                tracks         = tracks,
                isLoading      = tracksLoading,
                onBack         = { viewModel.closePlaylist() },
                onPlayPlaylist = { viewModel.playPlaylist(state.playlist) },
                onPlayTrack    = { track ->
                    val index = tracks.indexOfFirst { it.id == track.id }
                    when {
                        index < 0 -> viewModel.playTrack(track)
                        state.playlist.uri == "spotify:collection:tracks" ->
                            viewModel.playTracksFromLiked(index)
                        else -> viewModel.playTrackInPlaylist(state.playlist, index)
                    }
                },
            )
            SpotifyUiState.SearchResults -> SearchResultsScreen(
                query      = searchQuery,
                results    = searchResults,
                isSearching = isSearching,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onClear    = { viewModel.clearSearch() },
                onPlay     = { viewModel.playTrack(it) },
            )
            SpotifyUiState.Playlists -> PlaylistGrid(
                playlists    = playlists,
                searchQuery  = searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onOpen       = { viewModel.openPlaylist(it) },
                onLogout     = { viewModel.logout() },
            )
        }
    }
}

// ── Login ─────────────────────────────────────────────────────────────────────

@Composable
private fun LoginPrompt(authError: String?, onLogin: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Icon(Icons.Default.LibraryMusic, null, tint = SpotifyGreen, modifier = Modifier.size(72.dp))
            Text("Влез в Spotify", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                "За да виждаш и стартираш плейлисти директно от приложението",
                color = TextSecondary, fontSize = 14.sp,
            )
            if (authError != null) Text(authError, color = Color(0xFFFF5555), fontSize = 13.sp)
            Button(
                onClick  = onLogin,
                colors   = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                shape    = RoundedCornerShape(50),
                modifier = Modifier.height(52.dp).widthIn(min = 200.dp),
            ) {
                Text("Влез в Spotify", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ── Playlist grid ─────────────────────────────────────────────────────────────

@Composable
private fun PlaylistGrid(
    playlists:          List<SpotifyPlaylist>,
    searchQuery:        String,
    onSearchQueryChange: (String) -> Unit,
    onOpen:             (SpotifyPlaylist) -> Unit,
    onLogout:           () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(end = 220.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text("Spotify", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = onLogout) { Text("Изход", color = TextTertiary, fontSize = 13.sp) }
        }
        // Search field
        OutlinedTextField(
            value         = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder   = { Text("Търси песни в Spotify...", color = TextTertiary, fontSize = 14.sp) },
            leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextTertiary) },
            trailingIcon  = if (searchQuery.isNotBlank()) {{
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(Icons.Default.Close, null, tint = TextTertiary)
                }
            }} else null,
            singleLine    = true,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = SpotifyGreen,
                unfocusedBorderColor = Color(0xFF333355),
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary,
                cursorColor          = SpotifyGreen,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* already debounced */ }),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        )
        Text("Плейлисти", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
        LazyVerticalGrid(
            columns               = GridCells.Adaptive(minSize = 160.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(playlists, key = { it.id }) { playlist ->
                PlaylistCard(playlist = playlist, onClick = { onOpen(playlist) })
            }
        }
    }
}

@Composable
private fun PlaylistCard(playlist: SpotifyPlaylist, onClick: () -> Unit) {
    val isDark  = LocalAppColors.current.isDark
    val bgColor = if (isDark) Color(0xFF1A1A2A) else Color(0xFFF0F4FF)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, Color(0xFF333355), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (playlist.imageUrl != null) {
            AsyncImage(
                model = playlist.imageUrl, contentDescription = playlist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(SpotifyGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.LibraryMusic, null, tint = SpotifyGreen, modifier = Modifier.size(48.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            playlist.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        if (playlist.trackCount > 0) {
            Text("${playlist.trackCount} песни", color = TextTertiary, fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// ── Search results ────────────────────────────────────────────────────────────

@Composable
private fun SearchResultsScreen(
    query:        String,
    results:      List<SpotifyTrack>,
    isSearching:  Boolean,
    onQueryChange: (String) -> Unit,
    onClear:      () -> Unit,
    onPlay:       (SpotifyTrack) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value         = query,
            onValueChange = onQueryChange,
            placeholder   = { Text("Търси песни в Spotify...", color = TextTertiary, fontSize = 14.sp) },
            leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextTertiary) },
            trailingIcon  = {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, null, tint = TextTertiary)
                }
            },
            singleLine    = true,
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = SpotifyGreen,
                unfocusedBorderColor = Color(0xFF333355),
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary,
                cursorColor          = SpotifyGreen,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { }),
            modifier = Modifier.fillMaxWidth().padding(end = 220.dp, bottom = 8.dp),
        )
        when {
            isSearching -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SpotifyGreen)
            }
            results.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Няма резултати", color = TextTertiary, fontSize = 14.sp)
            }
            else -> {
                Text("Резултати", color = TextSecondary, fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp))
                LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(results, key = { it.id }) { track ->
                        TrackRow(track = track, onClick = { onPlay(track) })
                    }
                }
            }
        }
    }
}

// ── Playlist detail ───────────────────────────────────────────────────────────

@Composable
private fun PlaylistDetail(
    playlist:       SpotifyPlaylist,
    tracks:         List<SpotifyTrack>,
    isLoading:      Boolean,
    onBack:         () -> Unit,
    onPlayPlaylist: () -> Unit,
    onPlayTrack:    (SpotifyTrack) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        // Header — play button is left of title to avoid weather widget on the right
        Row(
            modifier          = Modifier.fillMaxWidth().padding(start = 4.dp, end = 220.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
            }
            // Play all button — next to back arrow, well away from weather widget
            IconButton(
                onClick  = onPlayPlaylist,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SpotifyGreen),
            ) {
                Icon(Icons.Default.PlayArrow, "Пусни плейлист", tint = Color.Black, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(10.dp))
            if (playlist.imageUrl != null) {
                AsyncImage(
                    model = playlist.imageUrl, contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(playlist.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (tracks.isNotEmpty()) {
                    Text("${tracks.size} песни", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        HorizontalDivider(color = Color(0xFF333355))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SpotifyGreen)
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
                items(tracks, key = { it.id }) { track ->
                    TrackRow(track = track, onClick = { onPlayTrack(track) })
                }
            }
        }
    }
}

@Composable
private fun TrackRow(track: SpotifyTrack, onClick: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (track.albumArt != null) {
            AsyncImage(
                model = track.albumArt, contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(6.dp)),
            )
        } else {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(6.dp))
                    .background(SpotifyGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.LibraryMusic, null, tint = SpotifyGreen, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(track.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = TextSecondary, fontSize = 12.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Text(formatDuration(track.durationMs), color = TextTertiary, fontSize = 12.sp)
    }
}

private fun formatDuration(ms: Long): String {
    val min = TimeUnit.MILLISECONDS.toMinutes(ms)
    val sec = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return "%d:%02d".format(min, sec)
}
