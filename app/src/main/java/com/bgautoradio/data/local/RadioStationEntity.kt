package com.bgautoradio.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.data.model.RadioStation

@Entity(tableName = "radio_stations")
data class RadioStationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String?,
    val country: String,
    val city: String?,
    val category: String,
    val tags: String,           // JSON array stored as string
    val websiteUrl: String?,
    val bitrate: Int?,
    val codec: String?,
    val isFavorite: Boolean,
    val isVerified: Boolean,
    val lastChecked: String?,
    val source: String?,
    val sortOrder: Int
)

fun RadioStationEntity.toDomain(): RadioStation = RadioStation(
    id          = id,
    name        = name,
    streamUrl   = streamUrl,
    logoUrl     = logoUrl,
    country     = country,
    city        = city,
    category    = RadioCategory.fromKey(category),
    tags        = tags.split(",").filter { it.isNotBlank() },
    websiteUrl  = websiteUrl,
    bitrate     = bitrate,
    codec       = codec,
    isFavorite  = isFavorite,
    isVerified  = isVerified,
    lastChecked = lastChecked,
    source      = source,
    sortOrder   = sortOrder
)

fun RadioStation.toEntity(): RadioStationEntity = RadioStationEntity(
    id          = id,
    name        = name,
    streamUrl   = streamUrl,
    logoUrl     = logoUrl,
    country     = country,
    city        = city,
    category    = category.key,
    tags        = tags.joinToString(","),
    websiteUrl  = websiteUrl,
    bitrate     = bitrate,
    codec       = codec,
    isFavorite  = isFavorite,
    isVerified  = isVerified,
    lastChecked = lastChecked,
    source      = source,
    sortOrder   = sortOrder
)
