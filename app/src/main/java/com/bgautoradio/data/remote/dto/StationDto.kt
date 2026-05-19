package com.bgautoradio.data.remote.dto

import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.data.model.RadioStation
import com.google.gson.annotations.SerializedName

data class StationCatalogDto(
    @SerializedName("version")   val version: Int              = 1,
    @SerializedName("updatedAt") val updatedAt: String?        = null,
    @SerializedName("stations")  val stations: List<StationDto> = emptyList()
)

data class StationDto(
    @SerializedName("id")          val id: String          = "",
    @SerializedName("name")        val name: String        = "",
    @SerializedName("streamUrl")   val streamUrl: String   = "",
    @SerializedName("logoUrl")     val logoUrl: String?    = null,
    @SerializedName("country")     val country: String     = "Bulgaria",
    @SerializedName("city")        val city: String?       = null,
    @SerializedName("category")    val category: String    = "other",
    @SerializedName("tags")        val tags: List<String>  = emptyList(),
    @SerializedName("websiteUrl")  val websiteUrl: String? = null,
    @SerializedName("bitrate")     val bitrate: Int?       = null,
    @SerializedName("codec")       val codec: String?      = null,
    @SerializedName("isVerified")  val isVerified: Boolean = false,
    @SerializedName("lastChecked") val lastChecked: String? = null,
    @SerializedName("source")      val source: String?     = null,
    @SerializedName("sortOrder")   val sortOrder: Int      = 999
)

fun StationDto.toDomain(isFavorite: Boolean = false): RadioStation = RadioStation(
    id           = id,
    name         = name,
    streamUrl    = streamUrl,
    logoUrl      = logoUrl,
    country      = country,
    city         = city,
    category     = RadioCategory.fromKey(category),
    tags         = tags,
    websiteUrl   = websiteUrl,
    bitrate      = bitrate,
    codec        = codec,
    isFavorite   = isFavorite,
    isVerified   = isVerified,
    lastChecked  = lastChecked,
    source       = source,
    sortOrder    = sortOrder
)
