package com.bgautoradio.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bgautoradio.ui.theme.AccentCyan
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var timeText by remember { mutableStateOf(currentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            timeText = currentTime()
            delay(1_000)
        }
    }

    Text(
        text     = timeText,
        modifier = modifier,
        style    = MaterialTheme.typography.headlineMedium.copy(
            color      = AccentCyan,
            fontWeight = FontWeight.Bold,
            fontSize   = 22.sp,
            letterSpacing = 2.sp
        )
    )
}

private fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
