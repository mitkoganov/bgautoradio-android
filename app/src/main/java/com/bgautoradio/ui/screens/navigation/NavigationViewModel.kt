package com.bgautoradio.ui.screens.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.BuildConfig
import com.bgautoradio.util.FlexiblePolyline
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

data class SearchResult(val name: String, val lat: Double, val lon: Double)

data class Maneuver(
    val action:        String,   // "depart","turn","continue","arrive" etc.
    val direction:     String?,  // "left","right","slightLeft","slightRight","uTurnLeft" etc.
    val instruction:   String,
    val distanceM:     Int,
    val offset:        Int,      // index into routePoints
    val nextStreet:    String?,
    val currentStreet: String?,
)

data class NavState(
    val currentLat:        Double             = 0.0,
    val currentLon:        Double             = 0.0,
    val bearing:           Float              = 0f,
    val destLat:           Double?            = null,
    val destLon:           Double?            = null,
    val destName:          String?            = null,
    val routePoints:       List<GeoPoint>     = emptyList(),
    val distanceM:         Int?               = null,
    val durationSec:       Int?               = null,
    val isLoadingRoute:    Boolean            = false,
    val isNavigating:      Boolean            = false,
    val searchResults:     List<SearchResult> = emptyList(),
    // maneuver / turn-by-turn
    val maneuvers:         List<Maneuver>     = emptyList(),
    val nextManeuverIdx:   Int                = 0,
    val distToNextTurnM:   Int?               = null,
    val currentStreet:     String?            = null,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class NavigationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
) : ViewModel() {

    private val _state = MutableStateFlow(NavState())
    val state: StateFlow<NavState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    init {
        startGps()
        viewModelScope.launch {
            _searchQuery
                .debounce(500L)
                .filter { it.length >= 2 }
                .collect { query -> searchNominatim(query) }
        }
    }

    private fun startGps() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager = lm

        val listener = LocationListener { loc ->
            _state.value = _state.value.copy(
                currentLat = loc.latitude,
                currentLon = loc.longitude,
                bearing    = if (loc.hasBearing()) loc.bearing else _state.value.bearing,
            )
            if (_state.value.isNavigating) updateManeuver(loc.latitude, loc.longitude)
        }
        locationListener = listener

        for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)) {
            try {
                if (lm.isProviderEnabled(provider))
                    lm.requestLocationUpdates(provider, 1_000L, 5f, listener)
            } catch (_: SecurityException) {}
        }

        for (provider in listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER,
        )) {
            try {
                val loc = lm.getLastKnownLocation(provider) ?: continue
                if (loc.latitude != 0.0 || loc.longitude != 0.0) {
                    _state.value = _state.value.copy(currentLat = loc.latitude, currentLon = loc.longitude)
                    break
                }
            } catch (_: SecurityException) {}
        }
    }

    private fun updateManeuver(lat: Double, lon: Double) {
        val s = _state.value
        if (s.routePoints.isEmpty() || s.maneuvers.isEmpty()) return

        // Find closest polyline point index
        val results = FloatArray(1)
        val closestIdx = s.routePoints.indices.minByOrNull { i ->
            val p = s.routePoints[i]
            Location.distanceBetween(lat, lon, p.latitude, p.longitude, results)
            results[0]
        } ?: return

        // Next maneuver with offset > closestIdx
        val nextIdx = s.maneuvers.indexOfFirst { it.offset > closestIdx }
        val nextMan = if (nextIdx >= 0) s.maneuvers[nextIdx] else null

        // Distance along polyline from closestIdx to next maneuver offset
        val distToTurn: Int? = nextMan?.let { man ->
            var d = 0.0
            val end = man.offset.coerceAtMost(s.routePoints.size - 1)
            for (i in closestIdx until end) {
                val a = s.routePoints[i]
                val b = s.routePoints[i + 1]
                Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results)
                d += results[0]
            }
            d.toInt()
        }

        // Current street = last maneuver whose offset <= closestIdx
        val currentStreet = s.maneuvers
            .lastOrNull { it.offset <= closestIdx }
            ?.currentStreet
            ?: s.maneuvers.firstOrNull()?.currentStreet

        _state.value = s.copy(
            nextManeuverIdx  = if (nextIdx >= 0) nextIdx else s.maneuvers.size - 1,
            distToNextTurnM  = distToTurn,
            currentStreet    = currentStreet,
        )
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _state.value = _state.value.copy(searchResults = emptyList())
        }
    }

    private fun searchNominatim(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = "https://nominatim.openstreetmap.org/search" +
                "?q=${query.encode()}&format=json&limit=5&accept-language=bg&addressdetails=1"
            val results = runCatching {
                val body = okHttpClient.newCall(
                    Request.Builder()
                        .url(url)
                        .header("User-Agent", "BulgarianAutoRadio/1.0")
                        .build()
                ).execute().body?.string() ?: return@runCatching emptyList()
                val arr = JsonParser.parseString(body).asJsonArray
                arr.mapNotNull { el ->
                    val obj         = el.asJsonObject
                    val displayName = obj.get("display_name")?.asString ?: return@mapNotNull null
                    val lat         = obj.get("lat")?.asDouble ?: return@mapNotNull null
                    val lon         = obj.get("lon")?.asDouble ?: return@mapNotNull null
                    SearchResult(name = displayName, lat = lat, lon = lon)
                }
            }.getOrDefault(emptyList())
            _state.value = _state.value.copy(searchResults = results)
        }
    }

    fun selectDestination(result: SearchResult) {
        _state.value = _state.value.copy(
            destLat        = result.lat,
            destLon        = result.lon,
            destName       = result.name,
            searchResults  = emptyList(),
            isLoadingRoute = true,
        )
        fetchRoute(result.lat, result.lon)
    }

    private fun fetchRoute(destLat: Double, destLon: Double) {
        val s = _state.value
        viewModelScope.launch(Dispatchers.IO) {
            val url = "https://router.hereapi.com/v8/routes" +
                "?transportMode=car" +
                "&origin=${s.currentLat},${s.currentLon}" +
                "&destination=$destLat,$destLon" +
                "&return=polyline,summary,turnByTurnActions" +
                "&apikey=${BuildConfig.HERE_API_KEY}"

            Log.d("NavRoute", "fetchRoute origin=${s.currentLat},${s.currentLon} dest=$destLat,$destLon")

            runCatching {
                val body = okHttpClient.newCall(Request.Builder().url(url).build())
                    .execute().body?.string() ?: return@runCatching
                Log.d("NavRoute", "response(200): ${body.take(300)}")

                val section = JsonParser.parseString(body).asJsonObject
                    .getAsJsonArray("routes")
                    ?.firstOrNull()?.asJsonObject
                    ?.getAsJsonArray("sections")
                    ?.firstOrNull()?.asJsonObject ?: return@runCatching

                val polylineStr = section.get("polyline")?.asString ?: return@runCatching
                val summary     = section.getAsJsonObject("summary")
                val distanceM   = summary?.get("length")?.asInt
                val durationSec = summary?.get("duration")?.asInt
                val points      = FlexiblePolyline.decode(polylineStr)

                val maneuvers = section.getAsJsonArray("turnByTurnActions")
                    ?.mapNotNull { el ->
                        val obj       = el.asJsonObject
                        val action    = obj.get("action")?.asString ?: return@mapNotNull null
                        val direction = obj.get("direction")?.asString
                        val instr     = obj.get("instruction")?.asString ?: ""
                        val length    = obj.get("length")?.asInt ?: 0
                        val offset    = obj.get("offset")?.asInt ?: 0
                        val nextStreet = obj.getAsJsonObject("nextRoad")
                            ?.getAsJsonArray("name")
                            ?.firstOrNull()?.asJsonObject?.get("value")?.asString
                        val curStreet = obj.getAsJsonObject("currentRoad")
                            ?.getAsJsonArray("name")
                            ?.firstOrNull()?.asJsonObject?.get("value")?.asString
                        Maneuver(action, direction, instr, length, offset, nextStreet, curStreet)
                    } ?: emptyList()

                val initStreet = maneuvers.firstOrNull()?.currentStreet

                Log.d("NavRoute", "polyline header: '${polylineStr.take(4)}' pt[0]: lat=${points.firstOrNull()?.latitude} lon=${points.firstOrNull()?.longitude}")
                Log.d("NavRoute", "decoded ${points.size} points, ${maneuvers.size} maneuvers")
                _state.value = _state.value.copy(
                    routePoints    = points,
                    distanceM      = distanceM,
                    durationSec    = durationSec,
                    isLoadingRoute = false,
                    maneuvers      = maneuvers,
                    nextManeuverIdx = 0,
                    distToNextTurnM = maneuvers.firstOrNull()?.distanceM,
                    currentStreet  = initStreet,
                )
            }.onFailure {
                Log.e("NavRoute", "fetchRoute failed", it)
                _state.value = _state.value.copy(isLoadingRoute = false)
            }
        }
    }

    fun startNavigation() {
        _state.value = _state.value.copy(isNavigating = true)
        val s = _state.value
        if (s.currentLat != 0.0) updateManeuver(s.currentLat, s.currentLon)
    }

    fun stopNavigation() {
        _state.value = _state.value.copy(isNavigating = false)
    }

    fun clearDestination() {
        _state.value = _state.value.copy(
            destLat         = null,
            destLon         = null,
            destName        = null,
            routePoints     = emptyList(),
            distanceM       = null,
            durationSec     = null,
            isLoadingRoute  = false,
            isNavigating    = false,
            searchResults   = emptyList(),
            maneuvers       = emptyList(),
            nextManeuverIdx = 0,
            distToNextTurnM = null,
            currentStreet   = null,
        )
    }

    override fun onCleared() {
        super.onCleared()
        locationListener?.let { locationManager?.removeUpdates(it) }
    }

    private fun String.encode() = java.net.URLEncoder.encode(this, "UTF-8")
}
