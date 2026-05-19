package com.bgautoradio.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.bgautoradio.BuildConfig
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

data class TrafficState(
    val avgJamFactor: Float,   // 0.0–10.0
    val avgSpeedKmh:  Int,
    val delayMinutes: Int,
    val city:         String?,
    val updatedAt:    String
)

private data class Probe(val dLat: Double, val dLon: Double)
private val PROBES = listOf(
    Probe(+0.06,  0.00),
    Probe(-0.06,  0.00),
    Probe( 0.00, -0.09),
    Probe( 0.00, +0.09),
    Probe(+0.04, +0.06),
    Probe(-0.04, -0.06),
)

@Singleton
class TrafficRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetchTraffic(): TrafficState? = withContext(Dispatchers.IO) {
        val (lat, lon) = getLocation() ?: return@withContext null
        val city = reverseGeocode(lat, lon)
        val (jf, speed, delay) = if (BuildConfig.HERE_API_KEY.isNotBlank()) {
            fetchAggregated(lat, lon)
        } else Triple(0f, 0, 0)
        TrafficState(
            avgJamFactor = jf,
            avgSpeedKmh  = speed,
            delayMinutes = delay,
            city         = city,
            updatedAt    = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
    }

    private suspend fun fetchAggregated(lat: Double, lon: Double): Triple<Float, Int, Int> {
        val jamFactors  = mutableListOf<Float>()
        val speeds      = mutableListOf<Int>()
        val delaysSec   = mutableListOf<Float>()

        for (probe in PROBES) {
            val url = "https://router.hereapi.com/v8/routes" +
                "?transportMode=car" +
                "&origin=$lat,$lon" +
                "&destination=${lat + probe.dLat},${lon + probe.dLon}" +
                "&return=summary" +
                "&apikey=${BuildConfig.HERE_API_KEY}"
            try {
                val body = okHttpClient.newCall(Request.Builder().url(url).build())
                    .execute().body?.string() ?: continue
                val summary = JsonParser.parseString(body).asJsonObject
                    .getAsJsonArray("routes")
                    ?.firstOrNull()?.asJsonObject
                    ?.getAsJsonArray("sections")
                    ?.firstOrNull()?.asJsonObject
                    ?.getAsJsonObject("summary") ?: continue

                val base    = summary["baseDuration"]?.asFloat ?: continue
                val current = summary["duration"]?.asFloat ?: base
                val length  = summary["length"]?.asFloat ?: 0f

                val speedMs  = if (current > 0f) length / current else 0f
                val delaySec = (current - base).coerceAtLeast(0f)
                val delay10  = if (base > 0f) (delaySec / base * 10f).coerceIn(0f, 10f) else 0f

                jamFactors.add(delay10)
                speeds.add((speedMs * 3.6f).roundToInt())
                delaysSec.add(delaySec)
            } catch (_: Exception) { continue }
        }

        if (jamFactors.isEmpty()) return Triple(0f, 0, 0)
        return Triple(
            jamFactors.average().toFloat(),
            speeds.average().roundToInt(),
            (delaysSec.average() / 60).roundToInt()
        )
    }

    private suspend fun reverseGeocode(lat: Double, lon: Double): String? {
        val url = "https://revgeocode.search.hereapi.com/v1/revgeocode" +
            "?at=$lat,$lon&lang=bg&apiKey=${BuildConfig.HERE_API_KEY}"
        return try {
            val body = okHttpClient.newCall(Request.Builder().url(url).build())
                .execute().body?.string() ?: return fallbackGeocode(lat, lon)
            val addr = JsonParser.parseString(body).asJsonObject
                .getAsJsonArray("items")
                ?.firstOrNull()?.asJsonObject
                ?.getAsJsonObject("address") ?: return fallbackGeocode(lat, lon)
            addr["city"]?.asString ?: addr["county"]?.asString ?: fallbackGeocode(lat, lon)
        } catch (_: Exception) { fallbackGeocode(lat, lon) }
    }

    private suspend fun fallbackGeocode(lat: Double, lon: Double): String? {
        val url = "https://nominatim.openstreetmap.org/reverse" +
            "?lat=$lat&lon=$lon&format=json&accept-language=bg"
        return try {
            val body = okHttpClient.newCall(
                Request.Builder().url(url).header("User-Agent", "BulgarianAutoRadio/1.0").build()
            ).execute().body?.string() ?: return null
            val addr = JsonParser.parseString(body).asJsonObject
                .getAsJsonObject("address") ?: return null
            addr["city"]?.asString ?: addr["town"]?.asString ?: addr["village"]?.asString
        } catch (_: Exception) { null }
    }

    private fun getLocation(): Pair<Double, Double>? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        for (provider in listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )) {
            try {
                val loc = lm.getLastKnownLocation(provider)
                if (loc != null) return loc.latitude to loc.longitude
            } catch (_: SecurityException) {}
        }
        return null
    }
}
