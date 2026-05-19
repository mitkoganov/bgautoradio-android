package com.bgautoradio.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RadioStationDao {

    @Query("SELECT * FROM radio_stations ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<RadioStationEntity>>

    @Query("SELECT * FROM radio_stations WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavorites(): Flow<List<RadioStationEntity>>

    @Query("SELECT * FROM radio_stations WHERE category = :category ORDER BY sortOrder ASC, name ASC")
    fun observeByCategory(category: String): Flow<List<RadioStationEntity>>

    @Query("""
        SELECT * FROM radio_stations
        WHERE name LIKE '%' || :query || '%'
           OR city LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
        ORDER BY sortOrder ASC, name ASC
    """)
    fun search(query: String): Flow<List<RadioStationEntity>>

    @Query("SELECT * FROM radio_stations WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): RadioStationEntity?

    @Query("SELECT COUNT(*) FROM radio_stations")
    suspend fun count(): Int

    @Upsert
    suspend fun upsertAll(stations: List<RadioStationEntity>)

    @Upsert
    suspend fun upsert(station: RadioStationEntity)

    @Query("UPDATE radio_stations SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM radio_stations WHERE id NOT IN (:keepIds) AND isFavorite = 0")
    suspend fun pruneNonFavorites(keepIds: List<String>)

    @Query("SELECT * FROM radio_stations WHERE isFavorite = 1 AND streamUrl != '' ORDER BY sortOrder ASC, name ASC")
    suspend fun getAllFavoritesOnce(): List<RadioStationEntity>

    @Query("SELECT * FROM radio_stations WHERE streamUrl != '' ORDER BY sortOrder ASC, name ASC")
    suspend fun getAllStationsOnce(): List<RadioStationEntity>

    @Query("SELECT DISTINCT city FROM radio_stations WHERE city IS NOT NULL ORDER BY city ASC")
    fun observeCities(): Flow<List<String>>
}
