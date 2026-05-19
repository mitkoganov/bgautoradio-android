package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.ui.theme.*

// ── Brand color map for known Bulgarian stations ──────────────────────────────
// (top gradient color, bottom gradient color, text/accent color)
private data class StationBrand(val top: Color, val bot: Color, val fg: Color, val abbr: String)

private val BRANDS: Map<String, StationBrand> = mapOf(
    "bg-radio"        to StationBrand(Color(0xFF1F2C5A), Color(0xFF0B1230), Color(0xFFFFD24D), "BG"),
    "radio-1"         to StationBrand(Color(0xFFBF0D18), Color(0xFF5A0A10), Color(0xFFFFFFFF), "Р1"),
    "radio-1-rock"    to StationBrand(Color(0xFF1A0A2E), Color(0xFF0A0015), Color(0xFFB06FFF), "R1R"),
    "njoy"            to StationBrand(Color(0xFFFF5A1F), Color(0xFF7A1B0A), Color(0xFFFFFFFF), "N-J"),
    "darik"           to StationBrand(Color(0xFF0E386E), Color(0xFF04132A), Color(0xFF5FB1FF), "DRK"),
    "darik-nostalgie" to StationBrand(Color(0xFF2B4A7A), Color(0xFF0A1F40), Color(0xFFBCD4F0), "DNO"),
    "city"            to StationBrand(Color(0xFFEC1E79), Color(0xFF3A0823), Color(0xFFFFFFFF), "CTY"),
    "vitosha"         to StationBrand(Color(0xFF1A6B3A), Color(0xFF062813), Color(0xFFFFFFFF), "ВИТ"),
    "z-rock"          to StationBrand(Color(0xFF1A1A1A), Color(0xFF050505), Color(0xFFFF2E2E), "Z"),
    "fresh"           to StationBrand(Color(0xFF00B39F), Color(0xFF022B27), Color(0xFFFFFFFF), "FRE"),
    "energy-nrj"      to StationBrand(Color(0xFFFFCC00), Color(0xFFB38A00), Color(0xFF000000), "NRJ"),
    "veselina"        to StationBrand(Color(0xFF9B1F6E), Color(0xFF3A0728), Color(0xFFFFFFFF), "ВЕС"),
    "the-voice"       to StationBrand(Color(0xFF0055A5), Color(0xFF001F3D), Color(0xFFFFFFFF), "VOI"),
    "fm-plus"         to StationBrand(Color(0xFF006B3C), Color(0xFF002918), Color(0xFF00FF99), "FM+"),
    "melody"          to StationBrand(Color(0xFF8B1A72), Color(0xFF3A0730), Color(0xFFFFB3E6), "MEL"),
    "magic-fm"        to StationBrand(Color(0xFF4B0082), Color(0xFF1A0030), Color(0xFFE040FB), "MGC"),
    "veronika"        to StationBrand(Color(0xFFB5106A), Color(0xFF500030), Color(0xFFFFFFFF), "ВЕР"),
    "fokus"           to StationBrand(Color(0xFF1A3A6B), Color(0xFF071525), Color(0xFF66B2FF), "ФОК"),
    "jazz-fm"         to StationBrand(Color(0xFF2C1810), Color(0xFF0A0602), Color(0xFFD4A855), "JZZ"),
    "classic-fm"      to StationBrand(Color(0xFF1A0A2A), Color(0xFF080010), Color(0xFFD4AF37), "CLS"),
    "tangra-mega-rock" to StationBrand(Color(0xFF1A1A1A), Color(0xFF000000), Color(0xFFFF6B00), "TMR"),
    "radio-nova"      to StationBrand(Color(0xFFA8123E), Color(0xFF3A0415), Color(0xFFFFFFFF), "NOV"),
    "nova-news"       to StationBrand(Color(0xFF003580), Color(0xFF001230), Color(0xFFFFFFFF), "NOV"),
    "bnr-horizont"    to StationBrand(Color(0xFF003D7A), Color(0xFF001030), Color(0xFFFFFFFF), "БНР"),
    "bnr-hristo-botev" to StationBrand(Color(0xFF1A3A6B), Color(0xFF071525), Color(0xFFFFD700), "ХБ"),
    "bnr-sofia"       to StationBrand(Color(0xFF0A2A50), Color(0xFF040F20), Color(0xFF88CCFF), "РСФ"),
    "bnr-plovdiv"     to StationBrand(Color(0xFF1A3A5C), Color(0xFF071520), Color(0xFF88CCFF), "РПЛ"),
    "bnr-varna"       to StationBrand(Color(0xFF0A3050), Color(0xFF041220), Color(0xFF66BBFF), "РВН"),
    "bnr-burgas"      to StationBrand(Color(0xFF0A2840), Color(0xFF041018), Color(0xFF88CCFF), "РБС"),
    "bnr-shumen"      to StationBrand(Color(0xFF1A2A50), Color(0xFF071020), Color(0xFF88CCFF), "РШМ"),
    "bnr-stara-zagora" to StationBrand(Color(0xFF152240), Color(0xFF050E18), Color(0xFF88CCFF), "РСЗ"),
    "bnr-blagoevgrad" to StationBrand(Color(0xFF1A3050), Color(0xFF071220), Color(0xFF88CCFF), "РБЛ"),
    "bnr-kardzali"    to StationBrand(Color(0xFF102040), Color(0xFF040C18), Color(0xFF88CCFF), "РКД"),
    "katra-fm"        to StationBrand(Color(0xFF6B1A3A), Color(0xFF280A15), Color(0xFFFFB3CC), "КТР"),
    "ultra-blagoevgrad" to StationBrand(Color(0xFF1A0A5A), Color(0xFF08042A), Color(0xFF9988FF), "ULT"),
    "maya-varna"      to StationBrand(Color(0xFF1A4A2A), Color(0xFF071510), Color(0xFF88FFAA), "МАЯ"),
    "dobrudja"        to StationBrand(Color(0xFF4A3A0A), Color(0xFF1A1502), Color(0xFFFFD700), "ДОБ"),
    "radio-999"       to StationBrand(Color(0xFF1A0A0A), Color(0xFF0A0404), Color(0xFFFF6666), "999"),
    "metrum"          to StationBrand(Color(0xFF0A1A3A), Color(0xFF040A18), Color(0xFF66AAFF), "МЕТ"),
    "alpha"           to StationBrand(Color(0xFF1A1A2A), Color(0xFF080810), Color(0xFFAA88FF), "ALF"),
    "folklor"         to StationBrand(Color(0xFF3A1A0A), Color(0xFF150802), Color(0xFFFFAA44), "ФОЛ"),
)

// Fallback: derive consistent colors from station name hash
private fun hashBrand(name: String): StationBrand {
    val h = name.hashCode()
    val hues = listOf(200, 220, 180, 280, 320, 160, 240, 30)
    val hue  = hues[((h % hues.size) + hues.size) % hues.size]
    val top  = hsvColor(hue, 0.7f, 0.35f)
    val bot  = hsvColor(hue, 0.8f, 0.12f)
    val abbr = name.filter { it.isLetter() }.take(3).uppercase()
    return StationBrand(top, bot, Color(0xFFFFFFFF), abbr)
}

private fun hsvColor(h: Int, s: Float, v: Float): Color {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f % 2) - 1))
    val m = v - c
    val (r, g, b) = when (h / 60) {
        0    -> Triple(c, x, 0f)
        1    -> Triple(x, c, 0f)
        2    -> Triple(0f, c, x)
        3    -> Triple(0f, x, c)
        4    -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m)
}

// ── Main composable ───────────────────────────────────────────────────────────

@Composable
fun StationLogo(
    station:      RadioStation,
    size:         Dp       = 120.dp,
    cornerRadius: Dp       = 16.dp,
    modifier:     Modifier = Modifier
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier         = modifier
            .size(size)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        if (!station.logoUrl.isNullOrBlank()) {
            val ctx = LocalContext.current
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(ctx)
                    .data(station.logoUrl)
                    .memoryCacheKey(station.id)
                    .diskCacheKey(station.id)
                    .build(),
                contentDescription = station.name,
                contentScale       = ContentScale.Fit,
                modifier           = Modifier
                    .fillMaxSize()
                    .padding((size.value * 0.08f).dp)
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Success -> {
                        BrandLogo(station = station, size = size, showText = false)
                        SubcomposeAsyncImageContent()
                    }
                    else -> BrandLogo(station = station, size = size)
                }
            }
        } else {
            BrandLogo(station = station, size = size)
        }
    }
}

@Composable
fun BrandLogo(station: RadioStation, size: Dp, modifier: Modifier = Modifier, showText: Boolean = true) {
    val brand = BRANDS[station.id] ?: hashBrand(station.name)
    val fontSize = when {
        brand.abbr.length <= 2 -> (size.value * 0.30f).coerceIn(14f, 56f)
        brand.abbr.length == 3 -> (size.value * 0.22f).coerceIn(12f, 42f)
        else                   -> (size.value * 0.16f).coerceIn(10f, 32f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors    = listOf(brand.top, brand.bot),
                    start     = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end       = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )
        if (showText) {
            Text(
                text       = brand.abbr,
                color      = brand.fg,
                fontSize   = fontSize.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign  = TextAlign.Center,
                maxLines   = 1
            )
        }
    }
}
