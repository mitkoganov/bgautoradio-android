package com.bgautoradio.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import com.bgautoradio.ui.theme.*
import kotlin.math.*

// ── Light theme: animated particle waves ─────────────────────────────────────

private data class ParticleCfg(
    val yFraction:  Float,
    val amplitude:  Float,
    val wlFraction: Float,
    val phaseShift: Float,
    val color:      Color,
    val alpha:      Float,
    val dotRadius:  Float = 2.5f,
    val glow:       Float = 14f,
    val density:    Int   = 280
)

private val PARTICLE_WAVES = listOf(
    ParticleCfg(0.52f, 85f, 1.30f, 0.0f, Color(0xFF229BFF), 0.55f, 2.8f, 16f, 300),
    ParticleCfg(0.62f, 65f, 0.88f, 0.8f, Color(0xFF8A4DFF), 0.45f, 2.2f, 14f, 250),
    ParticleCfg(0.44f, 48f, 1.60f, 1.5f, Color(0xFF22DFFF), 0.38f, 1.8f, 12f, 200),
    ParticleCfg(0.70f, 40f, 1.10f, 2.3f, Color(0xFFD94CFF), 0.28f, 1.5f, 10f, 180),
)

// ── Dark theme: perspective 3D floor ─────────────────────────────────────────

private val PERSPECTIVE_LINES = 5  // линии от хоризонта към ъглите

@Composable
fun WaveBackground(modifier: Modifier = Modifier) {
    val isDark = LocalAppColors.current.isDark

    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 14_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    // Hero glow pulse — само за тъмна тема
    val heroGlow by transition.animateFloat(
        initialValue  = 0.18f,
        targetValue   = 0.32f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 3_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroGlow"
    )

    val bg0 = LocalAppColors.current.bg0
    val bg1 = LocalAppColors.current.bg1

    Canvas(modifier = modifier.fillMaxSize()) {
        if (isDark) {
            drawDarkBackground(bg0, heroGlow, phase)
        } else {
            drawLightBackground(bg0, bg1, phase)
        }
    }
}

// ── Dark background ───────────────────────────────────────────────────────────

private fun DrawScope.drawDarkBackground(bg0: Color, heroGlow: Float, phase: Float) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.42f   // horizon at 42%

    // L1: Deep vertical gradient
    drawRect(
        brush = Brush.verticalGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF020407),
                0.36f to Color(0xFF06111F),
                0.52f to Color(0xFF081A30),
                1.00f to Color(0xFF010305)
            ),
            startY = 0f,
            endY   = h
        )
    )

    // L2: Horizon glow — wide ellipse at 42% height
    drawOval(
        brush = Brush.radialGradient(
            listOf(SpecHorizonGlow, Color.Transparent),
            center = Offset(w * 0.50f, floorY),
            radius = w * 0.60f
        ),
        topLeft = Offset(w * 0.02f, floorY - h * 0.14f),
        size    = Size(w * 0.96f, h * 0.28f)
    )

    // L3: Perspective dot floor
    drawPerspectiveFloor(floorY, phase)

    // L4: Perspective horizon lines
    drawPerspectiveLines(floorY)

    // L5a: Wide blue floor glow — full center band (hero + cards zone)
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color(0xFF2196FF).copy(alpha = heroGlow * 0.70f), Color.Transparent),
            center = Offset(w * 0.50f, floorY + h * 0.08f),
            radius = w * 0.55f
        ),
        topLeft = Offset(w * 0.05f, floorY - h * 0.04f),
        size    = Size(w * 0.90f, h * 0.45f)
    )

    // L5b: Hero card concentrated glow (left ~40%)
    drawOval(
        brush = Brush.radialGradient(
            listOf(Color(0xFF22DFFF).copy(alpha = heroGlow * 0.55f), Color.Transparent),
            center = Offset(w * 0.28f, floorY + h * 0.06f),
            radius = w * 0.28f
        ),
        topLeft = Offset(w * 0.02f, floorY - h * 0.02f),
        size    = Size(w * 0.52f, h * 0.36f)
    )

    // L6: Vignette — black overlay at edges
    drawVignette()
}

private fun DrawScope.drawPerspectiveFloor(floorStartY: Float, phase: Float) {
    val w = size.width
    val h = size.height
    val floorHeight = h - floorStartY
    val centerX = w / 2f
    val rows = 28

    for (row in 0 until rows) {
        val t = row / (rows - 1).toFloat()
        val tPow = t.pow(1.65f)
        val y = floorStartY + tPow * floorHeight

        val spacingPx = lerp(14f.dp.toPx(), 38f.dp.toPx(), t)
        val radiusPx  = lerp(0.60f.dp.toPx(), 1.40f.dp.toPx(), t)
        val rowAlpha  = lerp(0.10f, 0.50f, t)

        // Анимация — леко мърдане на dots
        val animOffset = sin(phase + row * 0.4f) * spacingPx * 0.04f

        var x = (centerX % spacingPx) + animOffset
        while (x < w) {
            val distFromCenter = abs(x - centerX) / (w * 0.55f)
            val sideFade = (1f - distFromCenter).coerceIn(0f, 1f)
            val alpha = rowAlpha * sideFade

            drawCircle(
                color  = SpecBrightBlue.copy(alpha = alpha),
                radius = radiusPx,
                center = Offset(x, y)
            )
            x += spacingPx
        }
    }
}

private fun DrawScope.drawPerspectiveLines(floorStartY: Float) {
    val w = size.width
    val h = size.height
    val originX = w * 0.50f
    val lineColor = Color(0xFF2196FF).copy(alpha = 0.07f)

    // 7 линии — от хоризонта надолу и встрани
    val targetXs = listOf(0f, w * 0.15f, w * 0.35f, w * 0.65f, w * 0.85f, w, w * 0.50f)
    for (tx in targetXs) {
        drawLine(
            color       = lineColor,
            start       = Offset(originX, floorStartY),
            end         = Offset(tx, h),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawVignette() {
    val w = size.width
    val h = size.height

    // Left
    drawRect(Brush.horizontalGradient(listOf(Color.Black.copy(0.45f), Color.Transparent),
        startX = 0f, endX = w * 0.18f))
    // Right
    drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Black.copy(0.45f)),
        startX = w * 0.82f, endX = w))
    // Top
    drawRect(Brush.verticalGradient(listOf(Color.Black.copy(0.38f), Color.Transparent),
        startY = 0f, endY = h * 0.18f))
    // Bottom
    drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.42f)),
        startY = h * 0.78f, endY = h))
}

// ── Light background — текущите wave particles ────────────────────────────────

private fun DrawScope.drawLightBackground(bg0: Color, bg1: Color, phase: Float) {
    drawRect(color = bg0)

    // Subtle central radial gradient
    drawCircle(
        brush  = Brush.radialGradient(
            colors = listOf(Color(0x1A1B5EB0), Color.Transparent),
            center = center,
            radius = size.width * 0.55f
        ),
        radius = size.width * 0.55f,
        center = center
    )

    // Hero-side glow — left 30%
    drawCircle(
        brush  = Brush.radialGradient(
            colors = listOf(Color(0x1522DFFF), Color(0x0A8A4DFF), Color.Transparent),
            center = Offset(size.width * 0.30f, size.height * 0.50f),
            radius = size.height * 0.80f
        ),
        radius = size.height * 0.80f,
        center = Offset(size.width * 0.30f, size.height * 0.50f)
    )

    PARTICLE_WAVES.forEach { cfg ->
        drawWaveParticles(cfg, phase)
    }
}

private fun DrawScope.drawWaveParticles(cfg: ParticleCfg, phase: Float) {
    val spacing    = size.width / cfg.density
    val wavelength = size.width * cfg.wlFraction

    for (i in 0..cfg.density) {
        val x      = i * spacing
        val angle  = (x / wavelength * 2f * PI.toFloat()) + cfg.phaseShift + phase
        val y      = size.height * cfg.yFraction + sin(angle) * cfg.amplitude
        val dotCenter = Offset(x, y)

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val fp    = paint.asFrameworkPaint()
            fp.isAntiAlias = true
            fp.color       = cfg.color.copy(alpha = 0f).toArgb()
            fp.maskFilter  = BlurMaskFilter(cfg.glow, BlurMaskFilter.Blur.NORMAL)
            paint.color    = cfg.color.copy(alpha = cfg.alpha * 0.8f)
            canvas.drawCircle(dotCenter, cfg.dotRadius * 1.5f, paint)
        }

        drawCircle(
            color  = cfg.color.copy(alpha = cfg.alpha),
            radius = cfg.dotRadius,
            center = dotCenter
        )
    }
}

// ── Utility ───────────────────────────────────────────────────────────────────

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1f)
private fun Float.pow(exp: Float) = this.toDouble().pow(exp.toDouble()).toFloat()
