package com.bgautoradio.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Internal static values — used in Theme.kt for Material color scheme ───────

internal val _DarkBg0    = Color(0xFF020407)
internal val _DarkBg1    = Color(0xFF06111F)
internal val _DarkBg2    = Color(0xFF081A30)
internal val _DarkPanel  = Color(0x0BFFFFFF)
internal val _DarkBorder  = Color(0x14FFFFFF)
internal val _DarkBorder2 = Color(0x2EFFFFFF)

internal val _DarkTextPrimary   = Color(0xFFF4F8FF)
internal val _DarkTextSecondary = Color(0xFFA9BEDB)
internal val _DarkTextTertiary  = Color(0xFF5D6E87)
internal val _DarkTextDisabled  = Color(0xFF3F4F66)

internal val _DarkAccent     = Color(0xFF22DFFF)
internal val _DarkAccentLine = Color(0xFF2196FF)
internal val _DarkAccentGlow   = Color(0x2622DFFF)
internal val _DarkAccentGlow25 = Color(0x4022DFFF)
internal val _DarkAccentDim  = Color(0xFF1188AA)
internal val _DarkPurple     = Color(0xFF8A4DFF)
internal val _DarkMagenta    = Color(0xFFD94CFF)
internal val _DarkDanger     = Color(0xFFFF5D7A)
internal val _DarkStatusOk   = Color(0xFF4CAF50)
internal val _DarkStatusAmber = Color(0xFFFFB300)
internal val _DarkCardElevated = Color(0xFF081628)
internal val _DarkGlassDark    = Color(0xCC06111F)

// ── Glass / glow constants — за 3D карти (не са в AppColors) ─────────────────
val SpecGlassDark         = Color(0xBD050E1C)   // rgba(5,14,28,0.74) hero card bg
val SpecGlassStrong       = Color(0xE0040A14)   // rgba(4,10,20,0.88) small card bg
val SpecActiveCyanOverlay = Color(0x2922DFFF)   // rgba(34,223,255,0.16) active overlay
val SpecBrightBlue        = Color(0xFF2196FF)   // floor dot color
val SpecHorizonGlow       = Color(0x59203570)   // horizon radial glow
internal val _DarkAccentAmber    = Color(0xFFFFB300)
internal val _DarkAccentAmberDim = Color(0xFFA07200)

internal val _LightBg0    = Color(0xFFF5F7FA)
internal val _LightBg1    = Color(0xFFEBEFF5)
internal val _LightBg2    = Color(0xFFDFE4EE)
internal val _LightPanel  = Color(0x0D000000)
internal val _LightBorder  = Color(0x1E1A3A7A)
internal val _LightBorder2 = Color(0x550088CC)

internal val _LightTextPrimary   = Color(0xFF0A1A3A)
internal val _LightTextSecondary = Color(0xFF2D4A7A)
internal val _LightTextTertiary  = Color(0xFF5A7AAA)
internal val _LightTextDisabled  = Color(0xFF8AAAD0)

internal val _LightAccent     = Color(0xFF0088CC)
internal val _LightAccentLine = Color(0xFF0055AA)
internal val _LightAccentGlow   = Color(0x260088CC)
internal val _LightAccentGlow25 = Color(0x400088CC)
internal val _LightAccentDim  = Color(0xFF006699)
internal val _LightPurple     = Color(0xFF6930CC)
internal val _LightMagenta    = Color(0xFFBB20CC)
internal val _LightDanger     = Color(0xFFCC2244)
internal val _LightStatusOk   = Color(0xFF2E7D32)
internal val _LightStatusAmber = Color(0xFFE65100)
internal val _LightCardElevated = Color(0xFFD5DCE9)
internal val _LightGlassDark    = Color(0xCCEBEFF5)
internal val _LightAccentAmber    = Color(0xFFE65100)
internal val _LightAccentAmberDim = Color(0xFF9E4000)

// ── AppColors data class ──────────────────────────────────────────────────────

data class AppColors(
    val bg0: Color,
    val bg1: Color,
    val bg2: Color,
    val panel: Color,
    val border: Color,
    val border2: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val accent: Color,
    val accentLine: Color,
    val accentGlow: Color,
    val accentGlow25: Color,
    val accentDim: Color,
    val purple: Color,
    val magenta: Color,
    val danger: Color,
    val statusOk: Color,
    val statusAmber: Color,
    val cardElevated: Color,
    val glassDark: Color,
    val accentAmber: Color,
    val accentAmberDim: Color,
    val isDark: Boolean
) {
    val textOnAccent: Color get() = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
}

fun darkAppColors() = AppColors(
    bg0 = _DarkBg0, bg1 = _DarkBg1, bg2 = _DarkBg2,
    panel = _DarkPanel, border = _DarkBorder, border2 = _DarkBorder2,
    textPrimary = _DarkTextPrimary, textSecondary = _DarkTextSecondary,
    textTertiary = _DarkTextTertiary, textDisabled = _DarkTextDisabled,
    accent = _DarkAccent, accentLine = _DarkAccentLine,
    accentGlow = _DarkAccentGlow, accentGlow25 = _DarkAccentGlow25, accentDim = _DarkAccentDim,
    purple = _DarkPurple, magenta = _DarkMagenta,
    danger = _DarkDanger, statusOk = _DarkStatusOk, statusAmber = _DarkStatusAmber,
    cardElevated = _DarkCardElevated, glassDark = _DarkGlassDark,
    accentAmber = _DarkAccentAmber, accentAmberDim = _DarkAccentAmberDim,
    isDark = true
)

fun lightAppColors() = AppColors(
    bg0 = _LightBg0, bg1 = _LightBg1, bg2 = _LightBg2,
    panel = _LightPanel, border = _LightBorder, border2 = _LightBorder2,
    textPrimary = _LightTextPrimary, textSecondary = _LightTextSecondary,
    textTertiary = _LightTextTertiary, textDisabled = _LightTextDisabled,
    accent = _LightAccent, accentLine = _LightAccentLine,
    accentGlow = _LightAccentGlow, accentGlow25 = _LightAccentGlow25, accentDim = _LightAccentDim,
    purple = _LightPurple, magenta = _LightMagenta,
    danger = _LightDanger, statusOk = _LightStatusOk, statusAmber = _LightStatusAmber,
    cardElevated = _LightCardElevated, glassDark = _LightGlassDark,
    accentAmber = _LightAccentAmber, accentAmberDim = _LightAccentAmberDim,
    isDark = false
)

val LocalAppColors = compositionLocalOf { darkAppColors() }

// ── Public API — @Composable getters, четат от LocalAppColors ─────────────────
// Всички composable файлове продължават да ползват color = TextPrimary, Accent и т.н.

val Bg0: Color        @Composable get() = LocalAppColors.current.bg0
val Bg1: Color        @Composable get() = LocalAppColors.current.bg1
val Bg2: Color        @Composable get() = LocalAppColors.current.bg2
val Panel: Color      @Composable get() = LocalAppColors.current.panel
val Border: Color     @Composable get() = LocalAppColors.current.border
val Border2: Color    @Composable get() = LocalAppColors.current.border2

val TextPrimary: Color   @Composable get() = LocalAppColors.current.textPrimary
val TextSecondary: Color @Composable get() = LocalAppColors.current.textSecondary
val TextTertiary: Color  @Composable get() = LocalAppColors.current.textTertiary
val TextDisabled: Color  @Composable get() = LocalAppColors.current.textDisabled

val Accent: Color      @Composable get() = LocalAppColors.current.accent
val AccentLine: Color  @Composable get() = LocalAppColors.current.accentLine
val AccentGlow: Color  @Composable get() = LocalAppColors.current.accentGlow
val AccentGlow25: Color @Composable get() = LocalAppColors.current.accentGlow25
val AccentDim: Color   @Composable get() = LocalAppColors.current.accentDim
val Purple: Color      @Composable get() = LocalAppColors.current.purple
val Magenta: Color     @Composable get() = LocalAppColors.current.magenta

val Danger: Color      @Composable get() = LocalAppColors.current.danger
val StatusOk: Color    @Composable get() = LocalAppColors.current.statusOk
val StatusAmber: Color @Composable get() = LocalAppColors.current.statusAmber

// ── Legacy aliases ────────────────────────────────────────────────────────────

val DeepSpaceBlack: Color @Composable get() = LocalAppColors.current.bg0
val DarkNavy: Color       @Composable get() = LocalAppColors.current.bg1
val CardSurface: Color    @Composable get() = LocalAppColors.current.bg2
val CardElevated: Color   @Composable get() = LocalAppColors.current.cardElevated
val GlassDark: Color      @Composable get() = LocalAppColors.current.glassDark

val AccentCyan: Color     @Composable get() = LocalAppColors.current.accent
val AccentCyanDim: Color  @Composable get() = LocalAppColors.current.accentDim
val AccentCyanGlow: Color @Composable get() = LocalAppColors.current.accentGlow
val AccentAmber: Color    @Composable get() = LocalAppColors.current.accentAmber
val AccentAmberDim: Color @Composable get() = LocalAppColors.current.accentAmberDim

val TextOnAccent: Color @Composable get() = LocalAppColors.current.textOnAccent
val StatusGreen: Color  @Composable get() = LocalAppColors.current.statusOk
val StatusRed: Color    @Composable get() = LocalAppColors.current.danger

val BorderSubtle: Color  @Composable get() = LocalAppColors.current.border
val BorderVisible: Color @Composable get() = LocalAppColors.current.border2
