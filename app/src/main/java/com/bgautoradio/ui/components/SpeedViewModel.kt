package com.bgautoradio.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.BuildConfig
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@HiltViewModel
class SpeedViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
) : ViewModel() {

    data class SpeedState(
        val speedKmh: Int  = 0,
        val limitKmh: Int? = null,
    )

    private val _state = MutableStateFlow(SpeedState())
    val state: StateFlow<SpeedState> = _state.asStateFlow()

    private var lastLimitLat  = 0.0
    private var lastLimitLon  = 0.0
    private var lastBearing: Float? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    // GPS speed smoothing: suppress transient zeros caused by brief GPS signal loss
    private var consecutiveZeros = 0
    private var lastValidSpeed   = 0

    init {
        startLocationUpdates()
        fetchInitialSpeedLimit()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager = lm

        val listener = LocationListener { location ->
            if (location.hasBearing()) lastBearing = location.bearing

            val raw = if (location.hasSpeed())
                (location.speed * 3.6f).roundToInt().coerceAtLeast(0) else 0

            // Require 2 consecutive zero readings (~2s) before displaying 0
            val kmh = when {
                raw > 0 -> {
                    consecutiveZeros = 0
                    lastValidSpeed   = raw
                    raw
                }
                consecutiveZeros < 2 -> {
                    consecutiveZeros++
                    lastValidSpeed
                }
                else -> 0
            }
            _state.value = _state.value.copy(speedKmh = kmh)

            // Re-query speed limit when moved >50m from last query point
            val dist = floatArrayOf(0f)
            Location.distanceBetween(
                lastLimitLat, lastLimitLon,
                location.latitude, location.longitude, dist
            )
            if (dist[0] > 50f || (lastLimitLat == 0.0 && lastLimitLon == 0.0)) {
                lastLimitLat = location.latitude
                lastLimitLon = location.longitude
                viewModelScope.launch(Dispatchers.IO) {
                    val limit = fetchSpeedLimit(location.latitude, location.longitude)
                    // Always update — null shows "--", not a stale value
                    _state.value = _state.value.copy(limitKmh = limit)
                }
            }
        }
        locationListener = listener

        for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            try {
                if (lm.isProviderEnabled(provider)) {
                    lm.requestLocationUpdates(provider, 1_000L, 5f, listener)
                }
            } catch (_: SecurityException) {}
        }
    }

    private fun fetchInitialSpeedLimit() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )
        viewModelScope.launch(Dispatchers.IO) {
            repeat(10) { attempt ->
                if (lastLimitLat != 0.0 || lastLimitLon != 0.0) return@launch
                for (provider in providers) {
                    try {
                        val loc = lm.getLastKnownLocation(provider) ?: continue
                        if (loc.latitude == 0.0 && loc.longitude == 0.0) continue
                        lastLimitLat = loc.latitude
                        lastLimitLon = loc.longitude
                        val limit = fetchSpeedLimit(loc.latitude, loc.longitude)
                        _state.value = _state.value.copy(limitKmh = limit)
                        return@launch
                    } catch (_: SecurityException) {}
                }
                if (attempt < 9) kotlinx.coroutines.delay(10_000)
            }
        }
    }

    // Primary: HERE Routing API v8 — proprietary map data, much better coverage than OSM.
    // Fallback: Overpass (OSM) — two-step 25m then 60m with pick-closest.
    private suspend fun fetchSpeedLimit(lat: Double, lon: Double): Int? {
        if (BuildConfig.HERE_API_KEY.isNotBlank()) {
            val hereLimit = fetchSpeedLimitHere(lat, lon)
            if (hereLimit != null) return hereLimit
        }
        val narrow = fetchWithRadius(lat, lon, radius = 25)
        if (narrow != null) return narrow
        return fetchWithRadius(lat, lon, radius = 60)
    }

    // Queries HERE Routing API v8 with a short route from current position to a point
    // 150m ahead (using last known bearing, or due north if bearing unavailable).
    // HERE snaps both points to the nearest road and returns spans with speedLimit.
    // speedLimit in spans is in m/s — multiply by 3.6 to convert to km/h.
    private fun fetchSpeedLimitHere(lat: Double, lon: Double): Int? {
        val bearingRad = Math.toRadians((lastBearing ?: 0f).toDouble())
        val dist = 150.0
        val lat2 = lat + (dist / 111320.0) * cos(bearingRad)
        val lon2 = lon + (dist / (111320.0 * cos(Math.toRadians(lat)))) * sin(bearingRad)

        val url = "https://router.hereapi.com/v8/routes" +
            "?transportMode=car" +
            "&origin=$lat,$lon" +
            "&destination=$lat2,$lon2" +
            "&return=polyline" +
            "&spans=speedLimit" +
            "&apikey=${BuildConfig.HERE_API_KEY}"
        return try {
            val body = okHttpClient.newCall(Request.Builder().url(url).build())
                .execute().body?.string() ?: return null
            val spans = JsonParser.parseString(body).asJsonObject
                .getAsJsonArray("routes")
                ?.firstOrNull()?.asJsonObject
                ?.getAsJsonArray("sections")
                ?.firstOrNull()?.asJsonObject
                ?.getAsJsonArray("spans") ?: return null
            val speedMs = spans.firstOrNull()?.asJsonObject
                ?.get("speedLimit")?.asDouble ?: return null
            (speedMs * 3.6).roundToInt().takeIf { it > 0 }
        } catch (_: Exception) { null }
    }

    // Queries all 3 Overpass mirrors in parallel. Returns as soon as the first responds.
    private suspend fun fetchWithRadius(lat: Double, lon: Double, radius: Int): Int? =
        coroutineScope {
            val mirrors = listOf(
                "https://overpass.kumi.systems/api/interpreter",
                "https://overpass.openstreetmap.ru/api/interpreter",
                "https://overpass.private.coffee/api/interpreter",
            )
            val ch = Channel<Int?>(Channel.UNLIMITED)
            val jobs = mirrors.map { url ->
                launch(Dispatchers.IO) {
                    val result = withTimeoutOrNull(3_000L) { queryOverpass(url, lat, lon, radius) }
                    ch.send(result)
                }
            }
            var received = 0
            var result: Int? = null
            while (received < mirrors.size) {
                val r = ch.receive()
                received++
                if (r != null) { result = r; break }
            }
            jobs.forEach { it.cancel() }
            ch.close()
            result
        }

    private fun queryOverpass(url: String, lat: Double, lon: Double, radius: Int): Int? {
        val query = "[out:json][timeout:3];" +
            "way(around:$radius,$lat,$lon)" +
            "[highway~\"^(motorway|trunk|primary|secondary|tertiary|residential|living_street|unclassified)\$\"]" +
            "[maxspeed];" +
            "out body center;"
        return try {
            val body = FormBody.Builder().add("data", query).build()
            val req  = Request.Builder().url(url).post(body).build()
            val resp = okHttpClient.newCall(req).execute()
            if (!resp.isSuccessful) return null
            val text = resp.body?.string() ?: return null
            parseClosest(text, lat, lon)
        } catch (_: Exception) { null }
    }

    private fun parseClosest(json: String, lat: Double, lon: Double): Int? {
        val elements = runCatching {
            JsonParser.parseString(json).asJsonObject.getAsJsonArray("elements")
        }.getOrNull() ?: return null
        if (elements.size() == 0) return null

        var bestLimit: Int? = null
        var bestDist = Double.MAX_VALUE

        elements.forEach { el ->
            val obj   = el?.asJsonObject ?: return@forEach
            val tags  = obj.getAsJsonObject("tags") ?: return@forEach
            val raw   = tags.get("maxspeed")?.asString ?: return@forEach
            val limit = raw.filter { it.isDigit() }.take(3).toIntOrNull() ?: return@forEach
            val center = obj.getAsJsonObject("center")
            val dist: Double = if (center != null) {
                val clat = center.get("lat")?.asDouble ?: lat
                val clon = center.get("lon")?.asDouble ?: lon
                val d = FloatArray(1)
                Location.distanceBetween(lat, lon, clat, clon, d)
                d[0].toDouble()
            } else Double.MAX_VALUE
            if (dist < bestDist) { bestDist = dist; bestLimit = limit }
        }
        return bestLimit
    }

    override fun onCleared() {
        super.onCleared()
        locationListener?.let { locationManager?.removeUpdates(it) }
    }
}
