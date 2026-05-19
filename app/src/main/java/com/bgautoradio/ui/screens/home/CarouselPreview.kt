package com.bgautoradio.ui.screens.home

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
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

// ─────────────────────────────────────────────────────────────────────────────
// Carousel: glass hero stage + info + neighbor tiles
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
        val neighborSize = (heroSize * 0.43f).coerceIn(90.dp, 135.dp)
        val nameSize     = ((maxHeight.value / 17f).coerceIn(26f, 40f) * fontScale).sp
        val rdsSize      = ((maxHeight.value / 26f).coerceIn(15f, 24f) * fontScale).sp
        val artistSize   = ((maxHeight.value / 34f).coerceIn(13f, 20f) * fontScale).sp

        Row(
            modifier          = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 72.dp, bottom = 20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── LEFT: 3D glass hero card ──────────────────────────────────────
            GlassHeroCard(
                station  = station,
                cardSize = heroSize,
                playing  = playing,
                modifier = Modifier.weight(0.42f)
            )

            Spacer(Modifier.width(36.dp))

            // ── RIGHT: Info + neighbor tiles ──────────────────────────────────
            Column(
                modifier            = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Station name + RDS + artist
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    if (trackTitle != null) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.MusicNote, null, tint = Accent, modifier = Modifier.size(20.dp))
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
                    if (artist != null) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Person, null, tint = TextTertiary, modifier = Modifier.size(17.dp))
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

                // Neighbor tiles — shifted 14dp lower for floor-standing look
                if (stations.size > 1) {
                    Row(
                        modifier              = Modifier.padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment     = Alignment.Bottom
                    ) {
                        listOf(1, 2, 3).forEach { offset ->
                            val nIdx = (currentIndex + offset).mod(stations.size)
                            key(stations[nIdx].id) {
                                GlassSmallCard(
                                    station  = stations[nIdx],
                                    cardSize = neighborSize,
                                    onClick  = { onSelect(nIdx) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Nav arrows
        GlassArrowButton(direction = -1, onClick = onPrev,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 6.dp))
        GlassArrowButton(direction =  1, onClick = onNext,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 6.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero card — 3D glossy physical tile  (reference: car_launcher_3d_effects.kt)
//
// Glow technique: sized Box siblings with Modifier.blur() inside a shared
// parent Box. Each blur Box has a colored solid background — blurring a
// shape produces a real soft glow spread, unlike drawBehind which is clipped.
//
// Reflection: graphicsLayer { scaleY=-1f } + blur(14dp) + BlendMode.DstIn
// canvas gradient acts as alpha-mask — erases the bottom of the reflection
// with no extra overlay Box needed.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GlassHeroCard(
    station:  RadioStation,
    cardSize: Dp,
    playing:  Boolean,
    modifier: Modifier = Modifier
) {
    val isDark      = LocalAppColors.current.isDark
    val accentColor = LocalAppColors.current.accent
    val radius      = 22.dp

    val glowTr = rememberInfiniteTransition(label = "hg")
    val glowA by glowTr.animateFloat(
        initialValue  = 0.72f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "ga"
    )

    val glassBg = if (isDark)
        Brush.linearGradient(
            listOf(
                Color.White.copy(0.10f),
                Color(0xFF081A30).copy(0.68f),
                Color.Black.copy(0.82f)
            ),
            start = Offset.Zero,
            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    else
        Brush.linearGradient(listOf(Color.White.copy(0.94f), Color(0xFFCBD8F2)))

    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer Box allocates extra height for reflection + shadow without
        // inflating the Row's layout measurements (only width = cardSize reported).
        Box(
            modifier         = Modifier.size(width = cardSize, height = cardSize + 88.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Reflection — subtle color glow, no duplicate card image
            ReflectionCard(
                station  = station,
                cardSize = cardSize,
                modifier = Modifier.offset(y = cardSize + 5.dp)
            )

            // Card body — glass surface with all overlay layers
            Box(
                modifier = Modifier
                    .size(cardSize)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        val r = radius.toPx()
                        val shadowBlur = 22.dp.toPx()
                        val glowBlur   = 36.dp.toPx()
                        val haloBlur   = 52.dp.toPx()

                        // Contact shadow
                        drawIntoCanvas { canvas ->
                            val p = Paint(); val fp = p.asFrameworkPaint()
                            fp.isAntiAlias = true
                            fp.color      = Color.Black.copy(0f).toArgb()
                            fp.maskFilter = BlurMaskFilter(shadowBlur, BlurMaskFilter.Blur.NORMAL)
                            p.color       = Color.Black.copy(0.55f)
                            canvas.drawOval(w * 0.09f, h * 0.86f, w * 0.91f, h * 1.20f, p)
                        }

                        // Blue atmospheric halo
                        drawIntoCanvas { canvas ->
                            val p = Paint(); val fp = p.asFrameworkPaint()
                            fp.isAntiAlias = true
                            fp.color      = Color(0xFF1A72FF).copy(0f).toArgb()
                            fp.maskFilter = BlurMaskFilter(haloBlur, BlurMaskFilter.Blur.NORMAL)
                            p.color       = Color(0xFF1A72FF).copy(0.20f * glowA)
                            canvas.drawRoundRect(-w * 0.10f, -h * 0.10f, w * 1.10f, h * 1.10f, r * 1.4f, r * 1.4f, p)
                        }

                        // Cyan card glow
                        drawIntoCanvas { canvas ->
                            val p = Paint(); val fp = p.asFrameworkPaint()
                            fp.isAntiAlias = true
                            fp.color      = accentColor.copy(0f).toArgb()
                            fp.maskFilter = BlurMaskFilter(glowBlur, BlurMaskFilter.Blur.NORMAL)
                            p.color       = accentColor.copy(0.38f * glowA)
                            canvas.drawRoundRect(-w * 0.06f, -h * 0.06f, w * 1.06f, h * 1.06f, r, r, p)
                        }
                    }
                    .clip(RoundedCornerShape(radius))
                    .background(glassBg)
                    .border(1.5.dp, accentColor.copy(0.92f), RoundedCornerShape(radius))
            ) {
                // Artwork with padding so logos are readable, not cropped
                Box(
                    modifier         = Modifier.fillMaxSize().padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StationLogo(
                        station      = station,
                        size         = cardSize - 20.dp,
                        cornerRadius = 14.dp
                    )
                }

                // Inner double-bevel border (white inner ring at 12% opacity)
                Box(
                    Modifier
                        .matchParentSize()
                        .border(1.dp, Color.White.copy(0.12f), RoundedCornerShape(radius - 2.dp))
                )

                // Diagonal gloss — triangular catch-light on top-left face
                DiagonalGlossOverlay(alpha = if (isDark) 0.24f else 0.30f)

                // Bottom edge shadow — matches small cards (0.45f, 22% height)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(cardSize * 0.22f)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(0.45f))
                        ))
                )

                // Stronger bottom cyan rim light (wider + brighter)
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .height(4.dp)
                        .width(cardSize * 0.85f)
                        .blur(4.dp)
                        .background(accentColor.copy(1.0f), RoundedCornerShape(50))
                )

                // Top inner bevel — white 2dp horizontal line
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(topStart = radius, topEnd = radius))
                        .background(Color.White.copy(0.62f))
                )

                // Left inner bevel — vertical gradient bright edge
                Box(
                    Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .clip(RoundedCornerShape(topStart = radius, bottomStart = radius))
                        .background(Brush.verticalGradient(
                            listOf(Color.White.copy(0.32f), Color.White.copy(0.04f))
                        ))
                )

                if (playing) {
                    Box(
                        modifier         = Modifier.align(Alignment.BottomEnd).padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EqualizerBars(bars = 4, playing = true, color = accentColor, height = 22.dp)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reflection — real blur + BlendMode.DstIn alpha mask
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ReflectionCard(
    station:  RadioStation,
    cardSize: Dp,
    modifier: Modifier = Modifier
) {
    val isDark = LocalAppColors.current.isDark

    // One subtle color gradient — simulates glossy floor reflection, no logo copy
    Box(
        modifier = modifier
            .size(width = cardSize * 0.88f, height = cardSize * 0.24f)
            .blur(cardSize * 0.10f)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xFF2060C0).copy(if (isDark) 0.22f else 0.14f),
                            0.55f to Color(0xFF1040A0).copy(if (isDark) 0.08f else 0.05f),
                            1.00f to Color.Transparent
                        )
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Diagonal gloss — triangular catch-light on the left face of a card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DiagonalGlossOverlay(alpha: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width * 0.72f, 0f)
            lineTo(size.width * 0.36f, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(
            path  = path,
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha), Color.Transparent),
                start  = Offset.Zero,
                end    = Offset(size.width, size.height)
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small station card — glass tile with shadow, glow, gloss, reflection
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GlassSmallCard(
    station:  RadioStation,
    cardSize: Dp,
    onClick:  () -> Unit
) {
    val isDark      = LocalAppColors.current.isDark
    val accentColor = LocalAppColors.current.accent

    val glassBg = if (isDark)
        Brush.linearGradient(
            colorStops = arrayOf(
                0.00f to Color(0xCC0B1A2C),
                1.00f to Color(0xF0020810)
            )
        )
    else
        Brush.linearGradient(listOf(Color.White.copy(0.90f), Color(0xFFD5E2F2)))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Shadow + glow + glass card ────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(cardSize)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val r = 13.dp.toPx()

                    // 1. Contact shadow
                    drawIntoCanvas { canvas ->
                        val p = Paint(); val fp = p.asFrameworkPaint()
                        fp.isAntiAlias = true
                        fp.color       = Color.Black.copy(0f).toArgb()
                        fp.maskFilter  = BlurMaskFilter(h * 0.16f, BlurMaskFilter.Blur.NORMAL)
                        p.color        = Color.Black.copy(0.70f)
                        canvas.drawOval(w * 0.09f, h * 0.86f, w * 0.91f, h * 1.20f, p)
                    }

                    // 2. Cyan card glow
                    drawIntoCanvas { canvas ->
                        val p = Paint(); val fp = p.asFrameworkPaint()
                        fp.isAntiAlias = true
                        fp.color       = accentColor.copy(0f).toArgb()
                        fp.maskFilter  = BlurMaskFilter(w * 0.24f, BlurMaskFilter.Blur.NORMAL)
                        p.color        = accentColor.copy(0.38f)
                        canvas.drawRoundRect(-w * 0.06f, -h * 0.06f, w * 1.06f, h * 1.06f, r, r, p)
                    }
                }
                .clip(RoundedCornerShape(13.dp))
                .background(glassBg)
                .border(1.2.dp, accentColor.copy(0.65f), RoundedCornerShape(13.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                )
        ) {
            // Image
            StationLogo(station = station, size = cardSize, cornerRadius = 13.dp)

            // Diagonal gloss
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0.00f to Color.White.copy(0.26f),
                                0.38f to Color.White.copy(0.09f),
                                0.52f to Color.Transparent
                            ),
                            start = Offset.Zero,
                            end   = Offset(cardSize.value * 1.8f, cardSize.value * 1.8f)
                        )
                    )
            )

            // Bottom edge shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardSize * 0.22f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(0.45f))
                        )
                    )
            )

            // Bottom cyan rim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardSize * 0.08f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, accentColor.copy(0.20f))
                        )
                    )
            )

            // Top inner highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 13.dp, topEnd = 13.dp))
                    .background(Color.White.copy(0.52f))
            )
        }

        // ── Mini reflection — subtle color glow only, no image copy ─────────
        Box(
            modifier = Modifier
                .size(width = cardSize * 0.88f, height = 28.dp)
                .blur(12.dp)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1A50A0).copy(if (isDark) 0.22f else 0.14f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text       = station.name,
            color      = if (isDark) TextSecondary else TextPrimary,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis,
            modifier   = Modifier.widthIn(max = cardSize)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3D glass arrow button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GlassArrowButton(
    direction: Int,
    onClick:   () -> Unit,
    modifier:  Modifier = Modifier
) {
    val accentColor = LocalAppColors.current.accent

    Box(
        modifier = modifier
            .size(56.dp)
            .drawBehind {
                val r = size.width / 2f
                // Contact shadow
                drawIntoCanvas { canvas ->
                    val p = Paint(); val fp = p.asFrameworkPaint()
                    fp.isAntiAlias = true
                    fp.color       = Color.Black.copy(0f).toArgb()
                    fp.maskFilter  = BlurMaskFilter(r * 0.60f, BlurMaskFilter.Blur.NORMAL)
                    p.color        = Color.Black.copy(0.60f)
                    canvas.drawCircle(Offset(r, r + r * 0.55f), r * 0.70f, p)
                }
                // Cyan glow
                drawIntoCanvas { canvas ->
                    val p = Paint(); val fp = p.asFrameworkPaint()
                    fp.isAntiAlias = true
                    fp.color       = accentColor.copy(0f).toArgb()
                    fp.maskFilter  = BlurMaskFilter(r * 0.65f, BlurMaskFilter.Blur.NORMAL)
                    p.color        = accentColor.copy(0.28f)
                    canvas.drawCircle(Offset(r, r), r * 0.95f, p)
                }
            }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xCC142846),
                        0.70f to Color(0xEE061428),
                        1.00f to Color(0xFA02080F)
                    ),
                    center = Offset(28f, 22f),
                    radius = 56f
                )
            )
            .border(1.dp, Color(0x5A5AA5F0), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Top bevel line inside
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .border(1.dp,
                    Brush.linearGradient(
                        listOf(Color.White.copy(0.30f), Color.Transparent),
                        start = Offset(0f, 0f), end = Offset(54f, 54f)
                    ),
                    CircleShape
                )
        )
        Icon(
            imageVector        = if (direction < 0) Icons.AutoMirrored.Filled.KeyboardArrowLeft
                                 else               Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = if (direction < 0) "Предишна" else "Следваща",
            tint               = Color(0xFFDCE8F8),
            modifier           = Modifier.size(36.dp)
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
    RadioStation("jazz-fm",  "Jazz FM",      "http://", city = "Sofia",    category = RadioCategory.JAZZ,     isFavorite = true),
)

@Preview(name = "Hero Carousel · 1790×720 — Playing",
    widthDp = 1790, heightDp = 720, showBackground = true, backgroundColor = 0xFF020407)
@Composable
private fun PreviewHero1790Playing() {
    AutoRadioTheme {
        CarouselNowPlaying(stations = previewStations, currentIndex = 2,
            playing = true, streamTitle = "Криско - Любов без граници",
            onPrev = {}, onNext = {}, onSelect = {})
    }
}

@Preview(name = "Hero Carousel · 1280×720 — Paused",
    widthDp = 1280, heightDp = 720, showBackground = true, backgroundColor = 0xFF020407)
@Composable
private fun PreviewHero1280Paused() {
    AutoRadioTheme {
        CarouselNowPlaying(stations = previewStations, currentIndex = 1,
            playing = false, streamTitle = null,
            onPrev = {}, onNext = {}, onSelect = {})
    }
}

@Preview(name = "Hero Carousel · 1790×720 — No RDS",
    widthDp = 1790, heightDp = 720, showBackground = true, backgroundColor = 0xFF020407)
@Composable
private fun PreviewHero1790NoRds() {
    AutoRadioTheme {
        CarouselNowPlaying(stations = previewStations, currentIndex = 0,
            playing = true, streamTitle = null,
            onPrev = {}, onNext = {}, onSelect = {})
    }
}
