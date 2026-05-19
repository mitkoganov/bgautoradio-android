package com.bgautoradio.data.model

import androidx.compose.ui.graphics.Color

data class WeatherData(
    val tempC: Int,
    val emoji: String,
    val tint:  Color
)

fun wmoToDisplay(code: Int, isDay: Boolean): Pair<String, Color> = when (code) {
    0            -> (if (isDay) "☀️" else "🌙") to if (isDay) Color(0xFFFFC107) else Color(0xFF90CAF9)
    1            -> "🌤"  to Color(0xFFFFCA28)
    2            -> "⛅"  to Color(0xFFB0BEC5)
    3            -> "☁️"  to Color(0xFF90A4AE)
    45, 48       -> "🌫"  to Color(0xFFCFD8DC)
    in 51..55    -> "🌦"  to Color(0xFF64B5F6)
    in 61..65    -> "🌧"  to Color(0xFF1E88E5)
    in 71..77    -> "❄️"  to Color(0xFFB3E5FC)
    80, 81, 82   -> "🌦"  to Color(0xFF42A5F5)
    85, 86       -> "🌨"  to Color(0xFFE3F2FD)
    95           -> "⛈"  to Color(0xFFCE93D8)
    96, 99       -> "⛈"  to Color(0xFFBA68C8)
    else         -> "🌡"  to Color(0xFF90A4AE)
}
