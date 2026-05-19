package com.bgautoradio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bgautoradio.ui.theme.Accent

@Composable
fun EqualizerBars(
    bars:    Int     = 4,
    playing: Boolean = true,
    color:   Color   = Accent,
    height:  Dp      = 22.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq")

    Row(
        modifier            = modifier.height(height),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment   = Alignment.Bottom
    ) {
        repeat(bars) { i ->
            val peak = if (i % 2 == 0) 0.9f else 0.65f
            val fraction by infiniteTransition.animateFloat(
                initialValue = 0.18f,
                targetValue  = peak,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 600 + (i % 3) * 180,
                        easing         = FastOutSlowInEasing
                    ),
                    repeatMode         = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(i * 130)
                ),
                label = "eq_$i"
            )
            val actualFraction = if (playing) fraction else 0.18f
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(actualFraction)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp)
                    )
            )
        }
    }
}
