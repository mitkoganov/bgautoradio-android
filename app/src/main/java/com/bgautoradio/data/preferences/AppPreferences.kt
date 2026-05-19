package com.bgautoradio.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.dataStore

    // Keys
    private object Keys {
        val LAST_STATION_ID     = stringPreferencesKey("last_station_id")
        val AUTO_PLAY_ON_START  = booleanPreferencesKey("auto_play_on_start")
        val AUTO_PLAY_ON_BOOT   = booleanPreferencesKey("auto_play_on_boot")
        val KEEP_SCREEN_AWAKE   = booleanPreferencesKey("keep_screen_awake")
        val MOBILE_DATA_WARNING = booleanPreferencesKey("mobile_data_warning")
        val THEME_MODE          = stringPreferencesKey("theme_mode")       // auto | day | night
        val START_SCREEN        = stringPreferencesKey("start_screen")     // home | stations | favorites
        val CATALOG_VERSION     = intPreferencesKey("catalog_version")
        val CATALOG_UPDATED_AT  = stringPreferencesKey("catalog_updated_at")
        val FONT_SCALE_TOPBAR   = floatPreferencesKey("font_scale_topbar")
        val FONT_SCALE_RAIL     = floatPreferencesKey("font_scale_rail")
        val FONT_SCALE_CAROUSEL = floatPreferencesKey("font_scale_carousel")
        // Navigation saved addresses — stored as "lat,lng,title"
        val NAV_HOME   = stringPreferencesKey("nav_home_address")
        val NAV_WORK   = stringPreferencesKey("nav_work_address")
        val PRESET_1_ID              = stringPreferencesKey("preset_1_id")
        val PRESET_2_ID              = stringPreferencesKey("preset_2_id")
        val SPOTIFY_ACCESS_TOKEN     = stringPreferencesKey("spotify_access_token")
        val SPOTIFY_TOKEN_EXPIRY     = longPreferencesKey("spotify_token_expiry")
        val SPOTIFY_REFRESH_TOKEN    = stringPreferencesKey("spotify_refresh_token")
    }

    val lastStationId: Flow<String?> = store.data.safeCatch().map { it[Keys.LAST_STATION_ID] }
    val autoPlayOnStart: Flow<Boolean> = store.data.safeCatch().map { it[Keys.AUTO_PLAY_ON_START] ?: true }
    val autoPlayOnBoot: Flow<Boolean>  = store.data.safeCatch().map { it[Keys.AUTO_PLAY_ON_BOOT] ?: false }
    val keepScreenAwake: Flow<Boolean> = store.data.safeCatch().map { it[Keys.KEEP_SCREEN_AWAKE] ?: true }
    val mobileDataWarning: Flow<Boolean> = store.data.safeCatch().map { it[Keys.MOBILE_DATA_WARNING] ?: true }
    val themeMode: Flow<String>        = store.data.safeCatch().map { it[Keys.THEME_MODE] ?: "auto" }
    val startScreen: Flow<String>      = store.data.safeCatch().map { it[Keys.START_SCREEN] ?: "home" }
    val catalogVersion: Flow<Int>      = store.data.safeCatch().map { it[Keys.CATALOG_VERSION] ?: 0 }
    val catalogUpdatedAt: Flow<String?> = store.data.safeCatch().map { it[Keys.CATALOG_UPDATED_AT] }
    val fontScaleTopBar:   Flow<Float> = store.data.safeCatch().map { it[Keys.FONT_SCALE_TOPBAR]   ?: 1.0f }
    val fontScaleRail:     Flow<Float> = store.data.safeCatch().map { it[Keys.FONT_SCALE_RAIL]     ?: 1.0f }
    val fontScaleCarousel: Flow<Float> = store.data.safeCatch().map { it[Keys.FONT_SCALE_CAROUSEL] ?: 1.0f }
    val navHome:   Flow<String?> = store.data.safeCatch().map { it[Keys.NAV_HOME] }
    val navWork:   Flow<String?> = store.data.safeCatch().map { it[Keys.NAV_WORK] }
    val preset1Id: Flow<String?> = store.data.safeCatch().map { it[Keys.PRESET_1_ID] }
    val preset2Id: Flow<String?> = store.data.safeCatch().map { it[Keys.PRESET_2_ID] }
    val spotifyAccessToken:  Flow<String> = store.data.safeCatch().map { it[Keys.SPOTIFY_ACCESS_TOKEN]  ?: "" }
    val spotifyTokenExpiry:  Flow<Long>  = store.data.safeCatch().map { it[Keys.SPOTIFY_TOKEN_EXPIRY]   ?: 0L }
    val spotifyRefreshToken: Flow<String> = store.data.safeCatch().map { it[Keys.SPOTIFY_REFRESH_TOKEN] ?: "" }

    suspend fun setLastStationId(id: String) = store.edit { it[Keys.LAST_STATION_ID] = id }
    suspend fun setAutoPlayOnStart(v: Boolean) = store.edit { it[Keys.AUTO_PLAY_ON_START] = v }
    suspend fun setAutoPlayOnBoot(v: Boolean)  = store.edit { it[Keys.AUTO_PLAY_ON_BOOT] = v }
    suspend fun setKeepScreenAwake(v: Boolean) = store.edit { it[Keys.KEEP_SCREEN_AWAKE] = v }
    suspend fun setMobileDataWarning(v: Boolean) = store.edit { it[Keys.MOBILE_DATA_WARNING] = v }
    suspend fun setThemeMode(v: String)        = store.edit { it[Keys.THEME_MODE] = v }
    suspend fun setStartScreen(v: String)      = store.edit { it[Keys.START_SCREEN] = v }
    suspend fun setCatalogVersion(v: Int)      = store.edit { it[Keys.CATALOG_VERSION] = v }
    suspend fun setCatalogUpdatedAt(v: String) = store.edit { it[Keys.CATALOG_UPDATED_AT] = v }
    suspend fun setFontScaleTopBar(v: Float)   = store.edit { it[Keys.FONT_SCALE_TOPBAR]   = v }
    suspend fun setFontScaleRail(v: Float)     = store.edit { it[Keys.FONT_SCALE_RAIL]     = v }
    suspend fun setFontScaleCarousel(v: Float) = store.edit { it[Keys.FONT_SCALE_CAROUSEL] = v }
    suspend fun setNavHome(lat: Double, lng: Double, title: String) =
        store.edit { it[Keys.NAV_HOME] = "$lat,$lng,$title" }
    suspend fun setNavWork(lat: Double, lng: Double, title: String) =
        store.edit { it[Keys.NAV_WORK] = "$lat,$lng,$title" }
    suspend fun setPreset1Id(id: String) = store.edit { it[Keys.PRESET_1_ID] = id }
    suspend fun setPreset2Id(id: String) = store.edit { it[Keys.PRESET_2_ID] = id }
    suspend fun saveSpotifyToken(token: String, expiryMs: Long, refreshToken: String? = null) = store.edit {
        it[Keys.SPOTIFY_ACCESS_TOKEN] = token
        it[Keys.SPOTIFY_TOKEN_EXPIRY] = expiryMs
        if (refreshToken != null) it[Keys.SPOTIFY_REFRESH_TOKEN] = refreshToken
    }
    suspend fun clearSpotifyToken() = store.edit {
        it.remove(Keys.SPOTIFY_ACCESS_TOKEN)
        it.remove(Keys.SPOTIFY_TOKEN_EXPIRY)
        it.remove(Keys.SPOTIFY_REFRESH_TOKEN)
    }

    private fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
        catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
}
