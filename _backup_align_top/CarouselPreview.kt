package com.bgautoradio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.ui.components.EqualizerBars
import com.bgautoradio.ui.components.StationLogo
import com.bgautoradio.ui.theme.*
import com.bgautoradio.ui.theme.neonGlowBorder

// ─────────────────────────────────────────────────────────────────────────────
// Hero layout: big logo left  |  name + RDS + neighbor logos right
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun CarouselNowPlaying(
    stations:     List<RadioStation>,
    currentIndex: Int,
    playing:      Boolean,
    streamTitle:  String?,
    onPrev:       () -> Unit,
    onNext:       () -> Unit,
    onSelect:     (Int) -> Unit,
    fontScale:    Float    = 1f,
    modifier:     Modifier = Modifier
) {
    if (stations.isEmpty()) return

    val station  = stations[currentIndex]
    val rawTitle = streamTitle.orEmpty().trim()
    val artist: String?
    val trackTitle: String?
    if (rawTitle.contains(" - ")) {
        artist     = rawTitle.substringBefore(" - ").trim()
        trackTitle = rawTitle.substringAfter(" - ").trim()
    } else {
        artist     = null
        trackTitle = rawTitle.ifBlank { null }
    }

    var dragOffset by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(currentIndex) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            dragOffset < -60f -> onNext()
                            dragOffset > 60f  -> onPrev()
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onHorizontalDrag = { _, delta -> dragOffset += delta }
                )
            }
    ) {
        val heroSize     = (maxHeight * 0.70f).coerceIn(180.dp, 320.dp)
        val neighborSize = (heroSize * 0.38f).coerceIn(78.dp, 128.dp)
        val nameSize     = ((maxHeight.value / 17f).coerceIn(26f, 40f) * fontScale).sp
        val rdsSize      = ((maxHeight.value / 26f).coerceIn(15f, 24f) * fontScale).sp
        val artistSize   = ((maxHeight.value / 34f).coerceIn(13f, 20f) * fontScale).sp

        Row(
            modifier          = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 72.dp, bottom = 20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── LEFT: Hero logo + reflection ──────────────────────────────────
            HeroLogo(
                station  = station,
                size     = heroSize,
                playing  = playing,
                modifier = Modifier.weight(0.42f)
            )

            Spacer(Modifier.width(36.dp))

            // ── RIGHT: Info + neighbors ───────────────────────────────────────
            Column(
                modifier            = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Station name + RDS
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Station name + equalizer
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text          = station.name,
                            color         = TextPrimary,
                            fontSize      = nameSize,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            maxLines      = 1,
                            overflow      = TextOverflow.Ellipsis,
                            modifier      = Modifier.weight(1f, fill = false)
                        )
                        if (playing) {
                            EqualizerBars(bars = 4, playing = true, color = Accent, height = 24.dp)
                        }
                    }

                    // RDS track title
                    if (trackTitle != null) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint     = Accent,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text       = trackTitle,
                                color      = TextPrimary,
                                fontSize   = rdsSize,
                                fontWeight = FontWeight.SemiBold,
                                maxLines   = 1,
                                overflow   = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Artist
                    if (artist != null) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint     = TextTertiary,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text     = artist,
                                color    = TextSecondary,
                                fontSize = artistSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Neighbor station logos
                if (stations.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment     = Alignment.Bottom
                    ) {
                        listOf(1, 2, 3).forEach { offset ->
                            val nIdx = (currentIndex + offset).mod(stations.size)
                            NeighborItem(
                                station = stations[nIdx],
                                size    = neighborSize,
                                onClick = { onSelect(nIdx) }
                            )
                        }
                    }
                }
            }
        }

        // ── Nav arrows ─────────────────────────────────────────────────────────
        NavArrow(
            direction = -1,
            onClick   = onPrev,
            modifier  = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
        )
        NavArrow(
            direction = 1,
            onClick   = onNext,
            modifier  = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero logo with reflection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroLogo(
    station:  RadioStation,
    size:     Dp,
    playing:  Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main logo with neon glow border
        Box(
            modifier = Modifier
                .neonGlowBorder(color = Accent, cornerRadius = 26.dp, glowRadius = 26f)
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(26.dp))
            ) {
                StationLogo(station = station, size = size, cornerRadius = 26.dp)
                // Glossy sheen — top gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.10f), Color.Transparent)
                            )
                        )
                )
                if (playing) {
                    Box(
                        modifier         = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EqualizerBars(bars = 4, playing = true, color = Accent, height = 22.dp)
                    }
                }
            }
        }

        // Reflection
        Box(
            modifier = Modifier
                .size(width = size, height = size * 0.22f)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
        ) {
            // Flipped logo layer
            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer { scaleY = -1f; alpha = 0.28f }
            ) {
                StationLogo(station = station, size = size, cornerRadius = 26.dp)
            }
            // Gradient fade over the reflection
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(listOf(Color.Transparent, Bg1.copy(alpha = 0.90f)))
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Neighbor station card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NeighborItem(
    station: RadioStation,
    size:    Dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .neonGlowBorder(
                    color        = Accent.copy(alpha = 0.5f),
                    cornerRadius = 12.dp,
                    glowRadius   = 12f,
                    borderWidth  = 1.dp
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                )
        ) {
            StationLogo(station = station, size = size, cornerRadius = 12.dp)
        }
        Text(
            text     = station.name,
            color    = TextTertiary,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = size)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Navigation arrow
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NavArrow(
    direction: Int,
    onClick:   () -> Unit,
    modifier:  Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0x55061220))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = if (direction < 0) Icons.AutoMirrored.Filled.KeyboardArrowLeft
                                 else               Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = if (direction < 0) "Предишна" else "Следваща",
            tint               = TextSecondary,
            modifier           = Modifier.size(42.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview data
// ─────────────────────────────────────────────────────────────────────────────

private val previewStations = listOf(
    RadioStation("darik",    "Дарик Радио",  "http://", city = "София",    category = RadioCategory.NEWS,     isFavorite = true),
    RadioStation("vitosha",  "Радио Витоша", "http://", city = "София",    category = RadioCategory.POP,      isFavorite = true),
    RadioStation("fresh",    "Fresh Radio",  "http://", city = "София",    category = RadioCategory.DANCE,    isFavorite = true),
    RadioStation("bg-radio", "БГ Радио",     "http://", city = "България", category = RadioCategory.NATIONAL, isFavorite = true),
    RadioStation("jazz-fm",  "Jazz FM",      "http://", city = "София",    category = RadioCategory.JAZZ,     isFavorite = true),
)

@Preview(
    name            = "Hero Carousel · 1790×720 — Playing",
    widthDp         = 1790,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF03070D
)
@Composable
private fun PreviewHero1790Playing() {
    AutoRadioTheme {
        CarouselNowPlaying(
            stations     = previewStations,
            currentIndex = 2,
            playing      = true,
            streamTitle  = "Криско - Любов без граници",
            onPrev = {}, onNext = {}, onSelect = {}
        )
    }
}

@Preview(
    name            = "Hero Carousel · 1280×720 — Paused",
    widthDp         = 1280,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF03070D
)
@Composable
private fun PreviewHero1280Paused() {
    AutoRadioTheme {
        CarouselNowPlaying(
            stations     = previewStations,
            currentIndex = 1,
            playing      = false,
            streamTitle  = null,
            onPrev = {}, onNext = {}, onSelect = {}
        )
    }
}

@Preview(
    name            = "Hero Carousel · 1790×720 — No RDS",
    widthDp         = 1790,
    heightDp        = 720,
    showBackground  = true,
    backgroundColor = 0xFF03070D
)
@Composable
private fun PreviewHero1790NoRds() {
    AutoRadioTheme {
        CarouselNowPlaying(
            stations     = previewStations,
            currentIndex = 0,
            playing      = true,
            streamTitle  = null,
            onPrev = {}, onNext = {}, onSelect = {}
        )
    }
}
