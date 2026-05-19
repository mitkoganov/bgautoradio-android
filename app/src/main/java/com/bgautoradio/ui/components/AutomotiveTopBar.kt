package com.bgautoradio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.data.model.WeatherData
import com.bgautoradio.ui.theme.*
import com.bgautoradio.ui.weather.WeatherViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private val BG_MONTHS = arrayOf(
    "Януари", "Февруари", "Март", "Април", "Май", "Юни",
    "Юли", "Август", "Септември", "Октомври", "Ноември", "Декември"
)

private fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

private fun currentDate(): String {
    val cal   = Calendar.getInstance()
    val day   = cal.get(Calendar.DAY_OF_MONTH)
    val month = BG_MONTHS[cal.get(Calendar.MONTH)]
    val year  = cal.get(Calendar.YEAR)
    return "$day $month $year"
}

@Composable
fun AutomotiveTopBar(
    currentRoute: String,
    onNav:        (String) -> Unit,
    modifier:     Modifier = Modifier,
    fontScale:    Float    = 1f,
    weatherVm:    WeatherViewModel = hiltViewModel()
) {
    val weather by weatherVm.weather.collectAsStateWithLifecycle()

    var clock by remember { mutableStateOf(currentTime()) }
    var date  by remember { mutableStateOf(currentDate()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            clock = currentTime()
            date  = currentDate()
        }
    }

    val bg1Color      = Bg1
    val accentLineCl  = AccentLine

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .drawBehind {
                drawRect(color = bg1Color)
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, accentLineCl.copy(alpha = 0.8f), Color.Transparent)
                    ),
                    start       = Offset(0f, size.height - 0.5f),
                    end         = Offset(size.width, size.height - 0.5f),
                    strokeWidth = 1.5f
                )
            }
    ) {
        Row(
            modifier              = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Left: empty — keeps center section centered ───────────────────
            Row(modifier = Modifier.weight(1f)) { }

            // ── Center: date + branding ────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier            = Modifier.weight(1f)
            ) {
                Text(
                    text          = date,
                    color         = TextSecondary,
                    fontSize      = (12 * fontScale).sp,
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text          = "BULGARIAN AUTO RADIO",
                    color         = TextDisabled,
                    fontSize      = (11 * fontScale).sp,
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 3.sp
                )
            }

            // ── Right: weather + clock + settings ─────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.weight(1f),
            ) {
                Spacer(Modifier.weight(1f))

                // Weather block — only shown when data is available
                weather?.let { w -> WeatherChip(w, fontScale) }

                // Clock
                Text(
                    text          = clock,
                    color         = TextSecondary,
                    fontSize      = (22 * fontScale).sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                // Settings
                TopBarBtn(
                    icon    = Icons.Default.Settings,
                    label   = null,
                    active  = currentRoute == "settings",
                    onClick = { onNav("settings") }
                )
            }
        }
    }
}

@Composable
fun TopRightOverlay(
    currentRoute: String,
    onNav:        (String) -> Unit,
    fontScale:    Float = 1f,
    weatherVm:    WeatherViewModel = hiltViewModel()
) {
    val weather by weatherVm.weather.collectAsStateWithLifecycle()
    var clock by remember { mutableStateOf(currentTime()) }
    LaunchedEffect(Unit) {
        while (true) { delay(30_000); clock = currentTime() }
    }

    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier              = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        weather?.let { w -> WeatherChip(w, fontScale) }
        Text(
            text          = clock,
            color         = TextSecondary,
            fontSize      = (22 * fontScale).sp,
            fontWeight    = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        TopBarBtn(
            icon    = Icons.Default.Settings,
            label   = null,
            active  = currentRoute == "settings",
            onClick = { onNav("settings") }
        )
    }
}

@Composable
internal fun WeatherChip(weather: WeatherData, fontScale: Float = 1f) {
    val isDark = LocalAppColors.current.isDark
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text     = weather.emoji,
            fontSize = (22 * fontScale).sp
        )
        Text(
            text       = "${weather.tempC}°",
            color      = if (isDark) weather.tint else TextPrimary,
            fontSize   = (17 * fontScale).sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun TopBarBtn(
    icon:    androidx.compose.ui.graphics.vector.ImageVector,
    label:   String?,
    active:  Boolean,
    onClick: () -> Unit
) {
    val tint = if (active) Accent else TextTertiary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = label,
            tint               = tint,
            modifier           = Modifier.size(22.dp)
        )
        if (label != null) {
            Text(
                text       = label,
                color      = tint,
                fontSize   = 16.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
