package com.bgautoradio.ui.launcher

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// CarRadioLauncherScreen
//
// Layer order:
//   1. BackgroundImageLayer — full-screen pre-rendered 3D image asset
//   2. Optional dim/bright overlay (baked into BackgroundImageLayer)
//   3. LeftSidebar
//   4. HeroStationCard
//   5. StationInfo
//   6. SmallStationCarousel
//   7. CarouselArrowButtons
//   8. TopStatusBar
//
// Layout:
//   BoxWithConstraints scales all positions from a 960×540dp base.
//   All x/y offsets are absolute from the screen left edge.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CarRadioLauncherScreen(
    themeMode:       CarThemeMode,
    selectedStation: LauncherStation,
    stations:        List<LauncherStation>,
    streamTitle:     String?           = null,
    playing:         Boolean           = false,
    selectedRoute:   String            = "home",
    onPrev:          () -> Unit        = {},
    onNext:          () -> Unit        = {},
    onStationSelect: (Int) -> Unit     = {},
    onNav:           (String) -> Unit  = {},
    onSettings:      () -> Unit        = {}
) {
    val theme = if (themeMode == CarThemeMode.Dark) darkCarTheme() else lightCarTheme()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val scaleX = maxWidth  / 960.dp
        val scaleY = maxHeight / 540.dp
        val scale  = minOf(scaleX, scaleY)

        // ── L1: Background ────────────────────────────────────────────────────
        BackgroundImageLayer(theme)

        // ── L2: Sidebar ───────────────────────────────────────────────────────
        LeftSidebar(
            theme         = theme,
            selectedRoute = selectedRoute,
            onNav         = onNav,
            scale         = scale
        )

        // ── L3: Hero card ─────────────────────────────────────────────────────
        HeroStationCard(
            theme    = theme,
            station  = selectedStation,
            playing  = playing,
            scale    = scale,
            modifier = Modifier.offset(x = 125.dp * scale, y = 54.dp * scale)
        )

        // ── L4: Station info ──────────────────────────────────────────────────
        StationInfo(
            theme       = theme,
            station     = selectedStation,
            streamTitle = streamTitle,
            playing     = playing,
            scale       = scale,
            modifier    = Modifier
                .offset(x = 390.dp * scale, y = 62.dp * scale)
                .widthIn(max = (maxWidth - 390.dp * scale - 48.dp * scale))
        )

        // ── L5: Small station carousel ────────────────────────────────────────
        val neighborStations = stations
            .filterNot { it.id == selectedStation.id }
            .take(3)

        SmallStationCarousel(
            theme    = theme,
            stations = neighborStations,
            onSelect = { idx ->
                val realIdx = stations.indexOf(neighborStations.getOrNull(idx))
                if (realIdx >= 0) onStationSelect(realIdx)
            },
            scale    = scale,
            modifier = Modifier.offset(x = 390.dp * scale, y = 245.dp * scale)
        )

        // ── L6: Left arrow ────────────────────────────────────────────────────
        CarouselArrowButton(
            direction = -1,
            theme     = theme,
            onClick   = onPrev,
            scale     = scale,
            modifier  = Modifier.offset(x = 83.dp * scale, y = 206.dp * scale)
        )

        // ── L7: Right arrow ───────────────────────────────────────────────────
        CarouselArrowButton(
            direction = 1,
            theme     = theme,
            onClick   = onNext,
            scale     = scale,
            modifier  = Modifier.offset(
                x = maxWidth - 58.dp * scale,
                y = 206.dp * scale
            )
        )

        // ── L8: Top status bar ────────────────────────────────────────────────
        TopStatusBar(
            theme      = theme,
            onSettings = onSettings,
            modifier   = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 24.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(
    name            = "Launcher · Dark · 1280×720",
    widthDp         = 1280,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF020407
)
@Composable
private fun PreviewDark1280() {
    CarRadioLauncherScreen(
        themeMode       = CarThemeMode.Dark,
        selectedStation = sampleLauncherStations[0],
        stations        = sampleLauncherStations,
        streamTitle     = "The Weeknd - Blinding Lights",
        playing         = true
    )
}

@Preview(
    name            = "Launcher · Light · 1280×720",
    widthDp         = 1280,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFFE8EDF5
)
@Composable
private fun PreviewLight1280() {
    CarRadioLauncherScreen(
        themeMode       = CarThemeMode.Light,
        selectedStation = sampleLauncherStations[1],
        stations        = sampleLauncherStations,
        playing         = false
    )
}

@Preview(
    name            = "Launcher · Dark · 1920×720",
    widthDp         = 1920,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF020407
)
@Composable
private fun PreviewDark1920() {
    CarRadioLauncherScreen(
        themeMode       = CarThemeMode.Dark,
        selectedStation = sampleLauncherStations[2],
        stations        = sampleLauncherStations,
        playing         = true
    )
}
