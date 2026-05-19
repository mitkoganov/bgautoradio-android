package com.bgautoradio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.repository.WazeAlert
import com.bgautoradio.data.repository.WazeAlertRepository
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WazeAlertBanner(modifier: Modifier = Modifier) {
    var alert by remember { mutableStateOf<WazeAlert?>(null) }

    LaunchedEffect(Unit) {
        WazeAlertRepository.alert.collectLatest { alert = it }
    }

    alert?.let { a ->
        val infiniteTransition = rememberInfiniteTransition(label = "blink")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue  = 0.25f,
            animationSpec = infiniteRepeatable(
                animation  = tween(600, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        val bgColor = when (a.type) {
            com.bgautoradio.data.repository.WazeAlertType.POLICE   -> Color(0xFF1565C0)
            com.bgautoradio.data.repository.WazeAlertType.ACCIDENT -> Color(0xFFB71C1C)
            com.bgautoradio.data.repository.WazeAlertType.HAZARD   -> Color(0xFFE65100)
            com.bgautoradio.data.repository.WazeAlertType.TRAFFIC  -> Color(0xFF6A1B9A)
            com.bgautoradio.data.repository.WazeAlertType.OTHER    -> Color(0xFF37474F)
        }

        Box(
            modifier = modifier
                .alpha(alpha)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor.copy(alpha = 0.88f))
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(a.type.emoji, fontSize = 20.sp)
                Text(
                    text       = a.text,
                    color      = Color.White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
