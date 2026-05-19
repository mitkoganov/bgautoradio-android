package com.bgautoradio.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.bgautoradio.data.model.WeatherData
import com.bgautoradio.data.model.wmoToDisplay
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetchWeather(): WeatherData? = withContext(Dispatchers.IO) {
        val (lat, lon) = getLocation() ?: return@withContext null
        try {
            val url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$lat&longitude=$lon&current_weather=true"
            val request  = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val body     = response.body?.string() ?: return@withContext null
            val cw       = JsonParser.parseString(body)
                .asJsonObject.getAsJsonObject("current_weather")
            val tempC  = cw["temperature"].asFloat.toInt()
            val code   = cw["weathercode"].asInt
            val isDay  = cw["is_day"].asInt == 1
            val (emoji, tint) = wmoToDisplay(code, isDay)
            WeatherData(tempC = tempC, emoji = emoji, tint = tint)
        } catch (_: Exception) {
            null
        }
    }

    private fun getLocation(): Pair<Double, Double>? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        for (provider in providers) {
            try {
                val loc = lm.getLastKnownLocation(provider)
                if (loc != null) return loc.latitude to loc.longitude
            } catch (_: SecurityException) {}
        }
        return null
    }
}
