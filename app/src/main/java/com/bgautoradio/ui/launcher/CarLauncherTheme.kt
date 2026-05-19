package com.bgautoradio.ui.launcher

import androidx.compose.ui.graphics.Color
import com.bgautoradio.R

enum class CarThemeMode { Dark, Light }

data class CarLauncherTheme(
    val backgroundRes:      Int,
    val primaryText:        Color,
    val secondaryText:      Color,
    val mutedText:          Color,
    val accent:             Color,
    val cardBackground:     Color,
    val cardBorder:         Color,
    val activeNavBg:        Color,
    val sidebarBackground:  Color,
    val iconActive:         Color,
    val iconInactive:       Color,
    val shadowColor:        Color,
    val isDark:             Boolean
)

// ── Alpha hex lookup: 0.46→75, 0.78→C7, 0.18→2E, 0.72→B8, 0.55→8C
//                     0.34→57, 0.62→9E, 0.14→24, 0.48→7A, 0.22→38

fun darkCarTheme() = CarLauncherTheme(
    backgroundRes     = R.drawable.bg_car_stage_dark,
    primaryText       = Color(0xFFF5F8FF),
    secondaryText     = Color(0xFFA9BED8),
    mutedText         = Color(0xFF61758C),
    accent            = Color(0xFF22DFFF),
    cardBackground    = Color(0x75030C18),   // rgba(3,12,24,0.46)
    cardBorder        = Color(0xC722DFFF),   // rgba(34,223,255,0.78)
    activeNavBg       = Color(0x2E22DFFF),   // rgba(34,223,255,0.18)
    sidebarBackground = Color(0xB801060E),   // rgba(1,6,14,0.72)
    iconActive        = Color(0xFF22DFFF),
    iconInactive      = Color(0xFF7E91AA),
    shadowColor       = Color(0x8C000000),   // black 0.55
    isDark            = true
)

fun lightCarTheme() = CarLauncherTheme(
    backgroundRes     = R.drawable.bg_car_stage_light,
    primaryText       = Color(0xFF10213A),
    secondaryText     = Color(0xFF5E7188),
    mutedText         = Color(0xFF7A8CA0),
    accent            = Color(0xFF009FEF),
    cardBackground    = Color(0x57FFFFFF),   // white 0.34
    cardBorder        = Color(0x9E009FEF),   // rgba(0,159,239,0.62)
    activeNavBg       = Color(0x24009FEF),   // rgba(0,159,239,0.14)
    sidebarBackground = Color(0x7AF5FAFF),   // rgba(245,250,255,0.48)
    iconActive        = Color(0xFF009FEF),
    iconInactive      = Color(0xFF6E7F91),
    shadowColor       = Color(0x38466482),   // rgba(70,100,130,0.22)
    isDark            = false
)
