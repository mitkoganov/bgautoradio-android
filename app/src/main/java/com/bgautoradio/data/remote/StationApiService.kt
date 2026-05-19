package com.bgautoradio.data.remote

import com.bgautoradio.data.remote.dto.StationCatalogDto
import retrofit2.http.GET
import retrofit2.http.Url

interface StationApiService {
    @GET
    suspend fun fetchCatalog(@Url url: String): StationCatalogDto
}
