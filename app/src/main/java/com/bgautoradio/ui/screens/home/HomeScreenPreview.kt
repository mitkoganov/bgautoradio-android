package com.bgautoradio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.ui.components.AutomotiveRail
import com.bgautoradio.ui.components.WaveBackground
import com.bgautoradio.ui.theme.*

private val previewFavorites = listOf(
    RadioStation("radio1",  "Радио 1",        "http://", city = "София",    category = RadioCategory.NATIONAL, isFavorite = true),
    RadioStation("energy",  "Радио Energy",   "http://", city = "София",    category = RadioCategory.DANCE,    isFavorite = true),
    RadioStation("city",    "Радио City",     "http://", city = "София",    category = RadioCategory.POP,      isFavorite = true),
    RadioStation("njoy",    "N-JOY",          "http://", city = "София",    category = RadioCategory.DANCE,    isFavorite = true),
    RadioStation("bg-radio","БГ Радио",       "http://", city = "България", category = RadioCategory.POP,      isFavorite = true),
)

@Composable
private fun MockTopBar() {
    val bg1Color     = Bg1
    val accentLineCl = AccentLine

    Box(
        modifier = Modifier
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
            Row(modifier = Modifier.weight(1f)) { }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier            = Modifier.weight(1f)
            ) {
                Text(
                    text          = "13 Май 2026",
                    color         = TextSecondary,
                    fontSize      = 12.sp,
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text          = "BULGARIAN AUTO RADIO",
                    color         = TextDisabled,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 3.sp
                )
            }

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.weight(1f)
            ) {
                Spacer(Modifier.weight(1f))
                Text("⛅", fontSize = 22.sp)
                Text(
                    text       = "18°",
                    color      = TextSecondary,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text          = "14:32",
                    color         = TextSecondary,
                    fontSize      = 22.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint     = TextTertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(
    name            = "Home · 1790×720 Glow",
    widthDp         = 1790,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF03070D
)
@Composable
private fun PreviewHome1790Glow() {
    AutoRadioTheme {
        Box(Modifier.fillMaxSize().background(Bg0)) {
            Column(Modifier.fillMaxSize()) {
                MockTopBar()
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    AutomotiveRail(
                        currentRoute = "home",
                        onNav        = {}
                    )
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        WaveBackground()
                        CarouselNowPlaying(
                            stations     = previewFavorites,
                            currentIndex = 1,
                            playing      = true,
                            streamTitle  = "AC/DC - Back in Black",
                            onPrev = {}, onNext = {}, onSelect = {}
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name            = "Home · 1280×720 Glow",
    widthDp         = 1280,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF03070D
)
@Composable
private fun PreviewHome1280Glow() {
    AutoRadioTheme {
        Box(Modifier.fillMaxSize().background(Bg0)) {
            Column(Modifier.fillMaxSize()) {
                MockTopBar()
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    AutomotiveRail(
                        currentRoute = "home",
                        onNav        = {}
                    )
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        WaveBackground()
                        CarouselNowPlaying(
                            stations     = previewFavorites,
                            currentIndex = 0,
                            playing      = false,
                            streamTitle  = null,
                            onPrev = {}, onNext = {}, onSelect = {}
                        )
                    }
                }
            }
        }
    }
}
