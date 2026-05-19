package com.bgautoradio.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import com.bgautoradio.BuildConfig
import com.bgautoradio.MainActivity
import com.bgautoradio.data.model.SpotifyPlaylist
import com.bgautoradio.data.model.SpotifyTrack
import com.bgautoradio.data.preferences.AppPreferences
import com.google.gson.JsonParser
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SpotifyRepo"

@Singleton
class SpotifyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs:             AppPreferences,
    private val httpClient:        OkHttpClient,
    private val externalMediaRepo: ExternalMediaRepository,
) {
    companion object {
        private val SCOPES = listOf(
            "app-remote-control",
            "user-library-read",
            "playlist-read-private",
            "playlist-read-collaborative",
            "streaming",
        )
    }

    // ── App Remote ────────────────────────────────────────────────────────────

    private var appRemote: SpotifyAppRemote? = null

    private fun ensureConnectedAndRun(onFailure: (() -> Unit)? = null, block: (SpotifyAppRemote) -> Unit) {
        val remote = appRemote
        if (remote != null && remote.isConnected) {
            block(remote)
            return
        }
        Log.d(TAG, "Connecting App Remote")
        SpotifyAppRemote.connect(
            context,
            ConnectionParams.Builder(BuildConfig.SPOTIFY_CLIENT_ID)
                .setRedirectUri(BuildConfig.SPOTIFY_REDIRECT_URI)
                .showAuthView(false)
                .build(),
            object : Connector.ConnectionListener {
                override fun onConnected(r: SpotifyAppRemote) {
                    Log.d(TAG, "App Remote connected")
                    appRemote = r
                    block(r)
                }
                override fun onFailure(error: Throwable) {
                    Log.w(TAG, "App Remote failed: ${error.message}")
                    onFailure?.invoke()
                }
            },
        )
    }

    private fun playViaAppRemote(uri: String) {
        ensureConnectedAndRun(onFailure = { fallbackIntent(uri) }) { it.playerApi.play(uri) }
    }

    private fun fallbackIntent(uri: String) {
        val spotifyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri)).apply {
            setPackage("com.spotify.music")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(spotifyIntent) }
        // Return to our app after Spotify handles the URI
        Handler(Looper.getMainLooper()).postDelayed({
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            runCatching { context.startActivity(mainIntent) }
        }, 1500)
    }

    fun disconnectAppRemote() {
        appRemote?.let { SpotifyAppRemote.disconnect(it) }
        appRemote = null
    }

    // ── PKCE state ────────────────────────────────────────────────────────────

    private var codeVerifier: String? = null

    private val _pendingCode  = MutableStateFlow<String?>(null)
    val pendingCode: StateFlow<String?> = _pendingCode.asStateFlow()

    private val _authExpired  = MutableStateFlow(false)
    val authExpired: StateFlow<Boolean> = _authExpired.asStateFlow()
    fun clearAuthExpired() { _authExpired.value = false }

    fun setPendingCode(code: String) { _pendingCode.value = code }
    fun clearPendingCode()           { _pendingCode.value = null }

    // ── Auth URL (PKCE) ───────────────────────────────────────────────────────

    fun buildAuthUrl(): String {
        val verifier  = generateCodeVerifier().also { codeVerifier = it }
        val challenge = generateCodeChallenge(verifier)
        return Uri.Builder()
            .scheme("https").authority("accounts.spotify.com").appendPath("authorize")
            .appendQueryParameter("response_type",         "code")
            .appendQueryParameter("client_id",             BuildConfig.SPOTIFY_CLIENT_ID)
            .appendQueryParameter("redirect_uri",          BuildConfig.SPOTIFY_REDIRECT_URI)
            .appendQueryParameter("scope",                 SCOPES.joinToString(" "))
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge",        challenge)
            .build().toString()
    }

    // ── Token exchange ────────────────────────────────────────────────────────

    suspend fun exchangeCodeForToken(code: String): Boolean = withContext(Dispatchers.IO) {
        val verifier = codeVerifier ?: return@withContext false
        codeVerifier = null
        val body = FormBody.Builder()
            .add("grant_type",    "authorization_code")
            .add("code",          code)
            .add("redirect_uri",  BuildConfig.SPOTIFY_REDIRECT_URI)
            .add("client_id",     BuildConfig.SPOTIFY_CLIENT_ID)
            .add("code_verifier", verifier)
            .build()
        val req = Request.Builder().url("https://accounts.spotify.com/api/token").post(body).build()
        val responseBody = try {
            httpClient.newCall(req).execute().use { it.body?.string() }
        } catch (_: Exception) { return@withContext false } ?: return@withContext false

        val json      = runCatching { JsonParser.parseString(responseBody).asJsonObject }.getOrNull()
                        ?: return@withContext false
        val token        = json.get("access_token")?.asString?.takeIf { it.isNotBlank() }
                          ?: return@withContext false
        val expiresIn    = json.get("expires_in")?.asLong ?: 3600L
        val refreshToken = json.get("refresh_token")?.takeIf { !it.isJsonNull }?.asString
        prefs.saveSpotifyToken(token, System.currentTimeMillis() + expiresIn * 1000L, refreshToken)
        true
    }

    // ── Session ───────────────────────────────────────────────────────────────

    suspend fun isLoggedIn(): Boolean = getValidToken() != null

    private suspend fun getValidToken(): String? = withContext(Dispatchers.IO) {
        val token  = prefs.spotifyAccessToken.first()
        val expiry = prefs.spotifyTokenExpiry.first()
        if (token.isNotBlank() && System.currentTimeMillis() < expiry - 60_000L) return@withContext token
        // Try refresh
        val refresh = prefs.spotifyRefreshToken.first().ifBlank {
            _authExpired.value = true
            return@withContext null
        }
        Log.d(TAG, "Access token expired — refreshing")
        val body = FormBody.Builder()
            .add("grant_type",    "refresh_token")
            .add("refresh_token", refresh)
            .add("client_id",     BuildConfig.SPOTIFY_CLIENT_ID)
            .build()
        val req = Request.Builder().url("https://accounts.spotify.com/api/token").post(body).build()
        val responseBody = try {
            httpClient.newCall(req).execute().use { it.body?.string() }
        } catch (_: Exception) { return@withContext null } ?: return@withContext null
        val json      = runCatching { JsonParser.parseString(responseBody).asJsonObject }.getOrNull()
                        ?: return@withContext null
        val newToken  = json.get("access_token")?.asString?.takeIf { it.isNotBlank() } ?: run {
            _authExpired.value = true
            return@withContext null
        }
        val expiresIn = json.get("expires_in")?.asLong ?: 3600L
        val newRefresh = json.get("refresh_token")?.takeIf { !it.isJsonNull }?.asString
        prefs.saveSpotifyToken(newToken, System.currentTimeMillis() + expiresIn * 1000L, newRefresh)
        Log.d(TAG, "Token refreshed successfully")
        newToken
    }

    suspend fun logout() {
        likedPlayback = null
        disconnectAppRemote()
        prefs.clearSpotifyToken()
    }

    // ── Web API ───────────────────────────────────────────────────────────────

    suspend fun getUserPlaylists(): List<SpotifyPlaylist> = withContext(Dispatchers.IO) {
        getValidToken() ?: return@withContext emptyList()
        listOf(SpotifyPlaylist(id = "liked", name = "Харесани песни",
            imageUrl = null, trackCount = -1, uri = "spotify:collection:tracks"))
    }

    @Suppress("unused")
    private suspend fun getUserPlaylistsFull(): List<SpotifyPlaylist> = withContext(Dispatchers.IO) {
        val token = getValidToken() ?: return@withContext emptyList()
        val result = mutableListOf<SpotifyPlaylist>()
        result += SpotifyPlaylist(id = "liked", name = "Харесани песни",
            imageUrl = null, trackCount = -1, uri = "spotify:collection:tracks")

        var url: String? = "https://api.spotify.com/v1/me/playlists?limit=50"
        while (url != null) {
            val req = Request.Builder().url(url).header("Authorization", "Bearer $token").build()
            val body = try { httpClient.newCall(req).execute().use { it.body?.string() } }
                       catch (_: Exception) { break } ?: break
            val json  = runCatching { JsonParser.parseString(body).asJsonObject }.getOrNull() ?: break
            val items = json.get("items")?.takeIf { it.isJsonArray }?.asJsonArray ?: break
            items.forEach { el ->
                val obj        = el?.takeIf { !it.isJsonNull }?.asJsonObject ?: return@forEach
                val id         = obj.get("id")?.asString   ?: return@forEach
                val name       = obj.get("name")?.asString ?: return@forEach
                val uri        = obj.get("uri")?.asString  ?: return@forEach
                val trackCount = obj.get("tracks")?.takeIf { it.isJsonObject }?.asJsonObject
                                   ?.get("total")?.asInt ?: 0
                val imageUrl   = obj.get("images")?.takeIf { it.isJsonArray }?.asJsonArray
                                   ?.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                                   ?.get("url")?.asString
                result += SpotifyPlaylist(id, name, imageUrl, trackCount, uri)
            }
            url = json.get("next")?.takeIf { !it.isJsonNull }?.asString
        }
        result
    }

    suspend fun getPlaylistTracks(playlistId: String): List<SpotifyTrack> = withContext(Dispatchers.IO) {
        val token = getValidToken() ?: return@withContext emptyList()
        val result = mutableListOf<SpotifyTrack>()
        val baseUrl = if (playlistId == "liked")
            "https://api.spotify.com/v1/me/tracks?limit=50"
        else
            "https://api.spotify.com/v1/playlists/$playlistId/tracks?limit=50&fields=next,items(track(id,name,uri,duration_ms,artists,album(images)))"

        var url: String? = baseUrl
        while (url != null) {
            val req = Request.Builder().url(url).header("Authorization", "Bearer $token").build()
            val response = try { httpClient.newCall(req).execute() } catch (_: Exception) { break }
            if (response.code == 401) {
                Log.w(TAG, "getPlaylistTracks 401 — token invalid, triggering re-login")
                response.close()
                _authExpired.value = true
                break
            }
            if (response.code == 403) {
                Log.w(TAG, "getPlaylistTracks 403 — playlist not accessible, skipping")
                response.close()
                break
            }
            val body = response.use { it.body?.string() } ?: break
            val json  = runCatching { JsonParser.parseString(body).asJsonObject }.getOrNull() ?: break
            val items = json.get("items")?.takeIf { it.isJsonArray }?.asJsonArray ?: break
            items.forEach { el ->
                val item  = el?.takeIf { it.isJsonObject }?.asJsonObject ?: return@forEach
                val track = (item.get("track") ?: item).takeIf { it.isJsonObject }?.asJsonObject ?: return@forEach
                val id    = track.get("id")?.asString   ?: return@forEach
                val title = track.get("name")?.asString ?: return@forEach
                val uri   = track.get("uri")?.asString  ?: return@forEach
                val durationMs = track.get("duration_ms")?.asLong ?: 0L
                val artist = track.get("artists")?.takeIf { it.isJsonArray }?.asJsonArray
                    ?.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                    ?.get("name")?.asString ?: ""
                val albumArt = track.get("album")?.takeIf { it.isJsonObject }?.asJsonObject
                    ?.get("images")?.takeIf { it.isJsonArray }?.asJsonArray
                    ?.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                    ?.get("url")?.asString
                result += SpotifyTrack(id, title, artist, albumArt, durationMs, uri)
            }
            url = json.get("next")?.takeIf { !it.isJsonNull }?.asString
        }
        result
    }

    // ── Playback ──────────────────────────────────────────────────────────────

    // Tracks the currently active liked-songs session so prev/next stay in sync.
    // Spotify App Remote history only records play() calls, not queue() items,
    // so skipPrevious() would jump back to the initial track instead of N-1.
    private data class LikedPlayback(val tracks: List<SpotifyTrack>, val currentIndex: Int)
    private var likedPlayback: LikedPlayback? = null

    fun playPlaylist(playlist: SpotifyPlaylist) = playViaAppRemote(playlist.uri)

    fun playTrack(track: SpotifyTrack) {
        likedPlayback = null
        playViaAppRemote(track.uri)
    }

    fun playTrackInPlaylist(playlistUri: String, index: Int) {
        likedPlayback = null
        ensureConnectedAndRun { remote ->
            remote.playerApi.skipToIndex(playlistUri, index)
                .setResultCallback { Log.d(TAG, "skipToIndex[$index] OK: $playlistUri") }
                .setErrorCallback  { err -> Log.w(TAG, "skipToIndex[$index] error: ${err.message} uri=$playlistUri") }
        }
    }

    // For liked songs (spotify:collection:tracks doesn't support skipToIndex)
    fun playTracksFromLiked(tracks: List<SpotifyTrack>, startIndex: Int) {
        likedPlayback = LikedPlayback(tracks, startIndex)
        ensureConnectedAndRun { remote ->
            remote.playerApi.play(tracks[startIndex].uri)
            val toQueue = tracks.drop(startIndex + 1).take(20)
            toQueue.forEach { remote.playerApi.queue(it.uri) }
            Log.d(TAG, "playTracksFromLiked: playing [$startIndex], queued ${toQueue.size} tracks")
        }
    }

    // App Remote transport controls (use when Spotify is the active source)
    fun remotePause() {
        ensureConnectedAndRun { remote ->
            remote.playerApi.pause()
                .setResultCallback { Log.d(TAG, "pause OK") }
                .setErrorCallback  { err -> Log.w(TAG, "pause error: ${err.message}") }
        }
    }
    fun remoteResume() {
        ensureConnectedAndRun { remote ->
            remote.playerApi.resume()
                .setResultCallback { Log.d(TAG, "resume OK") }
                .setErrorCallback  { err -> Log.w(TAG, "resume error: ${err.message}") }
        }
    }

    fun remoteSkipNext() {
        val lp = likedPlayback
        if (lp != null && lp.currentIndex + 1 < lp.tracks.size) {
            playTracksFromLiked(lp.tracks, lp.currentIndex + 1)
        } else {
            ensureConnectedAndRun { it.playerApi.skipNext() }
        }
    }

    fun remoteSkipPrev() {
        val lp = likedPlayback
        if (lp != null && lp.currentIndex - 1 >= 0) {
            playTracksFromLiked(lp.tracks, lp.currentIndex - 1)
        } else {
            ensureConnectedAndRun { it.playerApi.skipPrevious() }
        }
    }

    // ── Search ────────────────────────────────────────────────────────────────

    suspend fun searchTracks(query: String): List<SpotifyTrack> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val token = getValidToken() ?: return@withContext emptyList()
        val url   = Uri.Builder()
            .scheme("https").authority("api.spotify.com")
            .appendPath("v1").appendPath("search")
            .appendQueryParameter("q", query)
            .appendQueryParameter("type", "track")
            .appendQueryParameter("limit", "20")
            .build().toString()
        val req  = Request.Builder().url(url).header("Authorization", "Bearer $token").build()
        val body = try { httpClient.newCall(req).execute().use { it.body?.string() } }
                   catch (_: Exception) { return@withContext emptyList() } ?: return@withContext emptyList()
        val json   = runCatching { JsonParser.parseString(body).asJsonObject }.getOrNull()
                     ?: return@withContext emptyList()
        val items  = json.get("tracks")?.takeIf { it.isJsonObject }?.asJsonObject
                       ?.get("items")?.takeIf { it.isJsonArray }?.asJsonArray
                     ?: return@withContext emptyList()
        val result = mutableListOf<SpotifyTrack>()
        items.forEach { el ->
            val track    = el?.takeIf { it.isJsonObject }?.asJsonObject ?: return@forEach
            val id       = track.get("id")?.asString   ?: return@forEach
            val title    = track.get("name")?.asString ?: return@forEach
            val uri      = track.get("uri")?.asString  ?: return@forEach
            val durationMs = track.get("duration_ms")?.asLong ?: 0L
            val artist   = track.get("artists")?.takeIf { it.isJsonArray }?.asJsonArray
                ?.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                ?.get("name")?.asString ?: ""
            val albumArt = track.get("album")?.takeIf { it.isJsonObject }?.asJsonObject
                ?.get("images")?.takeIf { it.isJsonArray }?.asJsonArray
                ?.firstOrNull()?.takeIf { it.isJsonObject }?.asJsonObject
                ?.get("url")?.asString
            result += SpotifyTrack(id, title, artist, albumArt, durationMs, uri)
        }
        result
    }

    // ── PKCE helpers ──────────────────────────────────────────────────────────

    private fun generateCodeVerifier(): String {
        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun generateCodeChallenge(verifier: String): String {
        val hash = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(hash, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
