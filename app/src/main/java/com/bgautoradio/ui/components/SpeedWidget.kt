package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.ui.theme.LocalAppColors
import com.bgautoradio.ui.theme.TextSecondary

@Composable
fun SpeedWidget(
    modifier:  Modifier = Modifier,
    forceDark: Boolean  = false,
    vm:        SpeedViewModel = hiltViewModel(),
) {
    val state  by vm.state.collectAsStateWithLifecycle()
    val isDark = forceDark || LocalAppColors.current.isDark
    val speedColor = if (isDark) Color.White else Color(0xFF111111)

    Column(
        modifier            = modifier.padding(end = 16.dp, top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(5.dp, Color(0xFFDD1111), CircleShape)
        ) {
            Text(
                text       = state.limitKmh?.toString() ?: "--",
                color      = Color.Black,
                fontSize   = if ((state.limitKmh ?: 0) >= 100) 18.sp else 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 22.sp,
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = state.speedKmh.toString(),
                color      = speedColor,
                fontSize   = 34.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
            )
            Text(
                text     = "km/h",
                color    = TextSecondary,
                fontSize = 11.sp,
            )
        }
    }
}
