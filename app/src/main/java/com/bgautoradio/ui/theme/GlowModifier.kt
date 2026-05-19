package com.bgautoradio.ui.theme

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neonGlowBorder(
    color:        Color,
    cornerRadius: Dp    = 20.dp,
    glowRadius:   Float = 28f,
    borderWidth:  Dp    = 1.5.dp
): Modifier = this
    .drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val fp    = paint.asFrameworkPaint()
            fp.isAntiAlias = true
            fp.style       = android.graphics.Paint.Style.STROKE
            fp.strokeWidth = borderWidth.toPx() + glowRadius * 0.5f
            fp.color       = color.copy(alpha = 0f).toArgb()
            fp.maskFilter  = BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.NORMAL)
            paint.color    = color.copy(alpha = 0.80f)
            val inset  = glowRadius * 0.20f
            val radius = cornerRadius.toPx()
            canvas.drawRoundRect(
                left    = inset,
                top     = inset,
                right   = size.width - inset,
                bottom  = size.height - inset,
                radiusX = radius,
                radiusY = radius,
                paint   = paint
            )
        }
    }
    .border(borderWidth, color.copy(alpha = 0.60f), RoundedCornerShape(cornerRadius))
