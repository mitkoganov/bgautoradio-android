package com.bgautoradio.data.repository

import android.content.Context
import com.bgautoradio.BuildConfig
import com.bgautoradio.data.local.RadioStationDao
import com.bgautoradio.data.local.toDomain
import com.bgautoradio.data.local.toEntity
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.data.preferences.AppPreferences
import com.bgautoradio.data.remote.StationApiService
import com.bgautoradio.data.remote.dto.StationCatalogDto
import com.bgautoradio.data.remote.dto.toDomain
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: RadioStationDao,
    private val api: StationApiService,
    private val prefs: AppPreferences,
    private val gson: Gson
) {
    // ── Observe streams ──────────────────────────────────────────────────────

    fun observeAllStations(): Flow<List<RadioStation>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeFavorites(): Flow<List<RadioStation>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    fun observeByCategory(category: RadioCategory): Flow<List<RadioStation>> =
        dao.observeByCategory(category.key).map { list -> list.map { it.toDomain() } }

    fun observeCities(): Flow<List<String>> = dao.observeCities()

    fun search(query: String): Flow<List<RadioStation>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    // ── One-shot queries ─────────────────────────────────────────────────────

    suspend fun getStation(id: String): RadioStation? =
        dao.getById(id)?.toDomain()

    suspend fun getFavoritesOnce(): List<RadioStation> =
        dao.getAllFavoritesOnce().map { it.toDomain() }

    suspend fun getNavListOnce(): List<RadioStation> {
        val favs = dao.getAllFavoritesOnce().map { it.toDomain() }.filter { it.hasValidStream }
        if (favs.isNotEmpty()) return favs
        return dao.getAllStationsOnce().map { it.toDomain() }.filter { it.hasValidStream }
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    suspend fun toggleFavorite(station: RadioStation) {
        dao.setFavorite(station.id, !station.isFavorite)
    }

    suspend fun setFavorite(id: String, isFavorite: Boolean) =
        dao.setFavorite(id, isFavorite)

    // ── Initialization: local JSON then remote update ─────────────────────────

    suspend fun initializeCatalog() = withContext(Dispatchers.IO) {
        seedFromAssetIfNeeded()
        refreshFromRemote()
    }

    private suspend fun seedFromAssetIfNeeded() {
        val json = context.assets.open("bulgarian_radio_stations.json")
            .bufferedReader().readText()
        val catalog = gson.fromJson(json, StationCatalogDto::class.java)
        val storedVersion = prefs.catalogVersion.first()
        if (dao.count() == 0 || catalog.version > storedVersion) {
            mergeCatalog(catalog)
            prefs.setCatalogVersion(catalog.version)
            catalog.updatedAt?.let { prefs.setCatalogUpdatedAt(it) }
        }
    }

    suspend fun refreshFromRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val catalog = api.fetchCatalog(BuildConfig.STATIONS_REMOTE_URL)
            mergeCatalog(catalog)
            prefs.setCatalogVersion(catalog.version)
            catalog.updatedAt?.let { prefs.setCatalogUpdatedAt(it) }
            Unit
        }
    }

    private suspend fun mergeCatalog(catalog: StationCatalogDto) {
        // Preserve existing favorite flags
        val favIds = dao.getAllFavoritesOnce().map { it.id }.toSet()

        val entities = catalog.stations.map { dto ->
            dto.toDomain(isFavorite = dto.id in favIds).toEntity()
        }
        dao.upsertAll(entities)

        // Remove stale non-favorite entries not in the new catalog
        val newIds = catalog.stations.map { it.id }
        dao.pruneNonFavorites(newIds)
    }

    // ── Station health check ──────────────────────────────────────────────────

    suspend fun checkStreamHealth(station: RadioStation): Boolean =
        withContext(Dispatchers.IO) {
            if (station.streamUrl.isBlank()) return@withContext false
            runCatching {
                val url = java.net.URL(station.streamUrl)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 5_000
                connection.readTimeout    = 5_000
                connection.requestMethod  = "GET"
                connection.setRequestProperty("Icy-MetaData", "1")
                val code = connection.responseCode
                connection.disconnect()
                code in 200..299
            }.getOrDefault(false)
        }
}
