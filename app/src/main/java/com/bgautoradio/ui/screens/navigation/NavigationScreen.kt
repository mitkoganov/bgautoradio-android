package com.bgautoradio.ui.screens.navigation

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UTurnLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.BuildConfig
import com.bgautoradio.ui.components.SpeedWidget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val state        by viewModel.state.collectAsStateWithLifecycle()
    var searchText   by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val webViewRef  = remember { mutableStateOf<WebView?>(null) }
    val mapReadyRef = remember { mutableStateOf(false) }
    val mapReady    = mapReadyRef.value
    var zoom        by remember { mutableFloatStateOf(15f) }

    fun js(script: String) = webViewRef.value?.evaluateJavascript(script, null)
    fun pitch() = if (state.isNavigating) 60 else 0

    // ── GPS → navigation arrow ────────────────────────────────────────────────
    LaunchedEffect(state.currentLat, state.currentLon, state.bearing, mapReady) {
        if (!mapReady || (state.currentLat == 0.0 && state.currentLon == 0.0)) return@LaunchedEffect
        js("setCurrentLocation(${state.currentLat},${state.currentLon},${state.bearing})")
    }

    // ── Initial camera ───────────────────────────────────────────────────────
    LaunchedEffect(mapReady) {
        if (!mapReady) return@LaunchedEffect
        val lat = if (state.currentLat != 0.0) state.currentLat else 42.6977
        val lon = if (state.currentLon != 0.0) state.currentLon else 23.3219
        js("updateCamera($lat,$lon,$zoom,0,0,false)")
        if (state.currentLat != 0.0) js("setCurrentLocation($lat,$lon,${state.bearing})")
    }

    // ── Destination pin ──────────────────────────────────────────────────────
    LaunchedEffect(state.destLat, state.destLon, mapReady) {
        if (!mapReady) return@LaunchedEffect
        val lat = state.destLat; val lon = state.destLon
        if (lat != null && lon != null) js("setDestination($lat,$lon)")
        else js("setDestination(null,null)")
    }

    // ── Route line ───────────────────────────────────────────────────────────
    LaunchedEffect(state.routePoints, mapReady) {
        if (!mapReady) return@LaunchedEffect
        if (state.routePoints.isEmpty()) { js("setRoute('[]')"); return@LaunchedEffect }
        val json = state.routePoints.joinToString(",", "[", "]") { "[${it.latitude},${it.longitude}]" }
        js("setRoute('$json')")
        js("updateCamera(${state.currentLat},${state.currentLon},13,0,0,true)")
    }

    // ── Navigation camera follows GPS ────────────────────────────────────────
    LaunchedEffect(state.currentLat, state.currentLon, state.bearing, state.isNavigating, mapReady) {
        if (!mapReady || !state.isNavigating) return@LaunchedEffect
        if (state.currentLat == 0.0 && state.currentLon == 0.0) return@LaunchedEffect
        js("updateCamera(${state.currentLat},${state.currentLon},17,60,${state.bearing},true)")
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A2E))) {

        // ── Search (hidden while navigating) ─────────────────────────────────
        AnimatedVisibility(visible = !state.isNavigating, enter = fadeIn(), exit = fadeOut()) {
            Column {
                OutlinedTextField(
                    value         = searchText,
                    onValueChange = { text ->
                        searchText = text
                        viewModel.onSearchQueryChanged(text)
                    },
                    placeholder   = { Text("Търси адрес...", color = Color.White.copy(alpha = 0.5f)) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.7f)) },
                    trailingIcon  = if (searchText.isNotEmpty() || state.destName != null) {
                        {
                            IconButton(onClick = {
                                searchText = ""
                                viewModel.onSearchQueryChanged("")
                                if (state.destName != null) viewModel.clearDestination()
                                focusManager.clearFocus()
                            }) { Icon(Icons.Default.Close, null, tint = Color.White.copy(alpha = 0.7f)) }
                        }
                    } else null,
                    singleLine      = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = Color(0xFF2A2A3E),
                        unfocusedContainerColor = Color(0xFF1E1E30),
                        focusedBorderColor      = Color(0xFF00BCD4),
                        unfocusedBorderColor    = Color.White.copy(alpha = 0.2f),
                        focusedTextColor        = Color.White,
                        unfocusedTextColor      = Color.White,
                    ),
                    shape    = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 240.dp, top = 8.dp, bottom = 4.dp)
                        .height(56.dp),
                )

                AnimatedVisibility(visible = state.searchResults.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 240.dp, bottom = 4.dp)
                            .heightIn(max = 200.dp),
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(containerColor = Color(0xFF2A2A3E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        LazyColumn {
                            items(state.searchResults) { result ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchText = result.name.substringBefore(",")
                                            viewModel.selectDestination(result)
                                            focusManager.clearFocus()
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.Default.MyLocation, null,
                                        tint = Color(0xFF00BCD4), modifier = Modifier.size(18.dp))
                                    Text(
                                        text     = result.name,
                                        color    = Color.White,
                                        fontSize = 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                if (state.searchResults.last() != result)
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }
        }

        // ── Map ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        @Suppress("SetJavaScriptEnabled")
                        settings.allowUniversalAccessFromFileURLs = true
                        webViewClient = WebViewClient()
                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
                                Log.d("NavMap", "[JS] ${msg.message()} (${msg.sourceId()}:${msg.lineNumber()})")
                                return true
                            }
                        }
                        addJavascriptInterface(object {
                            @JavascriptInterface fun getApiKey() = BuildConfig.HERE_API_KEY
                            @JavascriptInterface fun onMapReady() {
                                Handler(Looper.getMainLooper()).post { mapReadyRef.value = true }
                            }
                        }, "Android")
                        val html = ctx.assets.open("map.html").bufferedReader().readText()
                        loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                        webViewRef.value = this
                    }
                }
            )

            // ── Turn indicator (shown while navigating) ───────────────────────
            val nextMan = state.maneuvers.getOrNull(state.nextManeuverIdx)
            if (state.isNavigating && nextMan != null && nextMan.action != "arrive") {
                TurnIndicator(
                    maneuver    = nextMan,
                    distToTurnM = state.distToNextTurnM,
                    modifier    = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 8.dp)
                        .widthIn(max = 260.dp)
                )
            }

            // ── Overlay zoom/locate controls ──────────────────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                MapBtn(Icons.Default.Add, "+") {
                    zoom = (zoom + 1f).coerceAtMost(20f)
                    js("updateCamera(${state.currentLat},${state.currentLon},$zoom,${pitch()},${state.bearing},true)")
                }
                MapBtn(Icons.Default.Remove, "-") {
                    zoom = (zoom - 1f).coerceAtLeast(5f)
                    js("updateCamera(${state.currentLat},${state.currentLon},$zoom,${pitch()},${state.bearing},true)")
                }
                MapBtn(Icons.Default.MyLocation, "Центрирай") {
                    js("updateCamera(${state.currentLat},${state.currentLon},$zoom,${pitch()},${state.bearing},true)")
                }
            }

            if (state.isLoadingRoute) {
                CircularProgressIndicator(
                    color    = Color(0xFF00BCD4),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Bottom-left overlay: destination / arrival info ───────────────
            if (state.destName != null) {
                BottomBar(
                    state    = state,
                    onStart  = { viewModel.startNavigation() },
                    onStop   = { viewModel.stopNavigation() },
                    onClear  = { viewModel.clearDestination(); searchText = "" },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 12.dp),
                )
            }

            // ── Bottom-right overlay: speed widget (while navigating) ─────────
            if (state.isNavigating) {
                SpeedWidget(
                    modifier  = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 8.dp),
                    forceDark = true,
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef.value?.apply { stopLoading(); destroy() }
            webViewRef.value = null
        }
    }
}

// ── Turn indicator card ───────────────────────────────────────────────────────

private fun maneuverIcon(action: String, direction: String?): ImageVector = when {
    action == "arrive"                                     -> Icons.Default.Place
    direction == "uTurnLeft" || direction == "uTurnRight" -> Icons.Default.UTurnLeft
    direction == "left"  || direction == "sharpLeft"      -> Icons.Default.ArrowBack
    direction == "right" || direction == "sharpRight"     -> Icons.Default.ArrowForward
    else                                                   -> Icons.Default.ArrowUpward
}

private fun distText(m: Int?): String = when {
    m == null    -> ""
    m >= 1000    -> "${"%.1f".format(m / 1000.0)} km"
    else         -> "$m m"
}

@Composable
private fun TurnIndicator(
    maneuver:    Maneuver,
    distToTurnM: Int?,
    modifier:    Modifier = Modifier,
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xEE1565C0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier             = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector       = maneuverIcon(maneuver.action, maneuver.direction),
                contentDescription = null,
                tint              = Color.White,
                modifier          = Modifier.size(32.dp),
            )
            Column {
                if (distToTurnM != null) {
                    Text(
                        text       = distText(distToTurnM),
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp,
                    )
                }
                val street = maneuver.nextStreet ?: ""
                if (street.isNotBlank()) {
                    Text(
                        text     = street,
                        color    = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

// ── Map button ────────────────────────────────────────────────────────────────

@Composable
private fun MapBtn(icon: ImageVector, label: String, onClick: () -> Unit) {
    FilledIconButton(
        onClick  = onClick,
        modifier = Modifier.size(48.dp),
        colors   = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color(0xCC1A1A2E),
            contentColor   = Color.White,
        )
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp))
    }
}

// ── Bottom bar ────────────────────────────────────────────────────────────────

@Composable
private fun BottomBar(
    state:    NavState,
    onStart:  () -> Unit,
    onStop:   () -> Unit,
    onClear:  () -> Unit,
    modifier: Modifier = Modifier,
) {
    val distText = state.distanceM?.let { m ->
        if (m >= 1000) "${"%.1f".format(m / 1000.0)} km" else "$m m"
    } ?: "--"
    val durText = state.durationSec?.let { sec ->
        val min = (sec / 60.0).roundToInt()
        if (min >= 60) "${min / 60}ч ${min % 60}мин" else "${min}мин"
    } ?: "--"
    val arrivalText = state.durationSec?.let { sec ->
        SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(Date(System.currentTimeMillis() + sec * 1000L))
    } ?: "--"

    Card(
        modifier  = modifier.widthIn(max = 230.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color(0xCC0D0D1A)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        if (state.isNavigating) {
            // compact: arrival + dur·dist | ✕ stop
            Row(
                modifier              = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column {
                    Text(
                        text       = arrivalText,
                        color      = Color.White,
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp,
                    )
                    Text(
                        text     = "$durText · $distText",
                        color    = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                    )
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onStop, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                }
            }
        } else {
            // browse: dest name + dist·dur | Start | ✕
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text       = state.destName?.substringBefore(",") ?: "",
                        color      = Color.White,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                        modifier   = Modifier.weight(1f),
                    )
                    IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFFFF5252), modifier = Modifier.size(15.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text     = "$distText · $durText",
                        color    = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick        = onStart,
                        enabled        = state.routePoints.isNotEmpty(),
                        colors         = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                        modifier       = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    ) {
                        Icon(Icons.Default.Navigation, null, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Старт", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
