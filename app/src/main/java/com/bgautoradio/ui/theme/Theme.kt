package com.bgautoradio.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun buildDarkColorScheme() = darkColorScheme(
    primary              = _DarkAccent,
    onPrimary            = Color.Black,
    primaryContainer     = _DarkAccentGlow,
    onPrimaryContainer   = _DarkAccent,
    secondary            = _DarkAccentLine,
    onSecondary          = Color.Black,
    secondaryContainer   = _DarkAccentGlow,
    onSecondaryContainer = _DarkTextPrimary,
    background           = _DarkBg0,
    onBackground         = _DarkTextPrimary,
    surface              = _DarkBg1,
    onSurface            = _DarkTextPrimary,
    surfaceVariant       = _DarkBg2,
    onSurfaceVariant     = _DarkTextSecondary,
    outline              = _DarkBorder2,
    outlineVariant       = _DarkBorder,
    error                = _DarkDanger,
    onError              = _DarkTextPrimary,
    inverseSurface       = _DarkTextPrimary,
    inverseOnSurface     = _DarkBg0,
    inversePrimary       = _DarkAccentDim,
    scrim                = _DarkGlassDark
)

private fun buildLightColorScheme() = lightColorScheme(
    primary              = _LightAccent,
    onPrimary            = Color.White,
    primaryContainer     = _LightAccentGlow,
    onPrimaryContainer   = _LightAccent,
    secondary            = _LightAccentLine,
    onSecondary          = Color.White,
    secondaryContainer   = _LightAccentGlow,
    onSecondaryContainer = _LightTextPrimary,
    background           = _LightBg0,
    onBackground         = _LightTextPrimary,
    surface              = _LightBg1,
    onSurface            = _LightTextPrimary,
    surfaceVariant       = _LightBg2,
    onSurfaceVariant     = _LightTextSecondary,
    outline              = _LightBorder2,
    outlineVariant       = _LightBorder,
    error                = _LightDanger,
    onError              = Color.White,
    inverseSurface       = _LightTextPrimary,
    inverseOnSurface     = _LightBg0,
    inversePrimary       = _LightAccentDim,
    scrim                = _LightGlassDark
)

@Composable
fun AutoRadioTheme(isDark: Boolean = true, content: @Composable () -> Unit) {
    val appColors    = if (isDark) darkAppColors() else lightAppColors()
    val colorScheme  = if (isDark) buildDarkColorScheme() else buildLightColorScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = appColors.bg0.toArgb()
            window.navigationBarColor = appColors.bg0.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AutoRadioTypography,
            content     = content
        )
    }
}
