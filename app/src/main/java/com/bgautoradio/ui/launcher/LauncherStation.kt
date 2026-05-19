package com.bgautoradio.ui.launcher

import androidx.compose.ui.graphics.Color
import com.bgautoradio.R

data class LauncherStation(
    val id:            String,
    val name:          String,
    val subtitle:      String,
    val artworkModel:  Any?,     // String URL, Int drawable res, or null
    val dominantColor: Color
)

val sampleLauncherStations = listOf(
    LauncherStation("magic_fm",      "Magic FM",      "Magic FM",      R.drawable.magic_fm,      Color(0xFF6A1B9A)),
    LauncherStation("radio_vitosha", "Радио Витоша",  "Радио Витоша",  R.drawable.radio_vitosha, Color(0xFF0E7A37)),
    LauncherStation("fresh",         "Fresh! Dance",  "Fresh! Dance",  R.drawable.radio_fresh,   Color(0xFF135CB8)),
    LauncherStation("radio_1",       "Радио 1",       "Радио 1",       R.drawable.radio_1,       Color(0xFFB80E0E)),
)

fun com.bgautoradio.data.model.RadioStation.toLauncher(): LauncherStation = LauncherStation(
    id           = id,
    name         = name,
    subtitle     = name,
    artworkModel = logoUrl?.takeIf { it.isNotBlank() },
    dominantColor = Color(0xFF22DFFF)
)
