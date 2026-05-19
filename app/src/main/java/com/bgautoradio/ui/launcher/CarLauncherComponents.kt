package com.bgautoradio.ui.launcher

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bgautoradio.ui.components.EqualizerBars

// ─────────────────────────────────────────────────────────────────────────────
// Background image — full screen, pre-rendered 3D environment
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BackgroundImageLayer(theme: CarLauncherTheme, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model              = theme.backgroundRes,
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )
        // Very subtle contrast overlay — does not dim the 3D background
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    if (theme.isDark) Color.Black.copy(0.08f)
                    else              Color.White.copy(0.06f)
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SubtleGlowBox — reusable glow wrapper using Modifier.blur() technique
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SubtleGlowBox(
    glowColor:  Color,
    glowAlpha:  Float,
    blurRadius: Dp,
    shape:      Shape,
    modifier:   Modifier = Modifier,
    content:    @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        Box(
            Modifier
                .matchParentSize()
                .blur(blurRadius)
                .background(glowColor.copy(glowAlpha), shape)
        )
        Box(Modifier.matchParentSize(), content = content)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Left sidebar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LeftSidebar(
    theme:         CarLauncherTheme,
    selectedRoute: String,
    onNav:         (String) -> Unit,
    scale:         Float,
    modifier:      Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(76.dp * scale)
            .fillMaxHeight()
            .background(theme.sidebarBackground)
            .drawBehind {
                val x = size.width - 0.5.dp.toPx()
                drawLine(
                    color       = if (theme.isDark) Color(0x1A22DFFF) else Color(0x1E0064B4),
                    start       = Offset(x, 0f),
                    end         = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        // "В ефир"
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 54.dp * scale)
        ) {
            SidebarItem(
                theme    = theme,
                icon     = Icons.Default.Radio,
                label    = "В ефир",
                isActive = selectedRoute == "home",
                onClick  = { onNav("home") },
                scale    = scale
            )
        }

        // "Станции"
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 135.dp * scale)
        ) {
            SidebarItem(
                theme    = theme,
                icon     = Icons.Default.LibraryMusic,
                label    = "Станции",
                isActive = selectedRoute == "channels",
                onClick  = { onNav("channels") },
                scale    = scale
            )
        }

        // "Начало" — pinned near bottom
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 395.dp * scale)
        ) {
            SidebarItem(
                theme    = theme,
                icon     = Icons.Default.Home,
                label    = "Начало",
                isActive = false,
                onClick  = { onNav("launcher") },
                scale    = scale
            )
        }
    }
}

@Composable
fun SidebarItem(
    theme:    CarLauncherTheme,
    icon:     ImageVector,
    label:    String,
    isActive: Boolean,
    onClick:  () -> Unit,
    scale:    Float,
    modifier: Modifier = Modifier
) {
    val itemW  = 68.dp * scale
    val itemH  = 64.dp * scale
    val radius = 11.dp * scale

    Box(modifier = modifier.size(width = itemW, height = itemH)) {
        // Glow layer behind active item
        if (isActive) {
            Box(
                Modifier
                    .matchParentSize()
                    .blur(18.dp)
                    .background(
                        theme.accent.copy(if (theme.isDark) 0.35f else 0.18f),
                        RoundedCornerShape(radius)
                    )
            )
        }

        // Item surface
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(radius))
                .then(
                    if (isActive)
                        Modifier
                            .background(theme.activeNavBg)
                            .border(1.dp, theme.cardBorder.copy(0.70f), RoundedCornerShape(radius))
                    else Modifier
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = label,
                    tint               = if (isActive) theme.iconActive else theme.iconInactive,
                    modifier           = Modifier.size(if (isActive) 26.dp * scale else 23.dp * scale)
                )
                Text(
                    text       = label,
                    color      = if (isActive) theme.iconActive else theme.iconInactive,
                    fontSize   = (11 * scale).sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines   = 1
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero station card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HeroStationCard(
    theme:   CarLauncherTheme,
    station: LauncherStation,
    playing: Boolean,
    scale:   Float,
    modifier: Modifier = Modifier
) {
    val cardSize  = 248.dp * scale
    val radius    = 18.dp * scale

    Box(
        modifier         = Modifier
            .size(width = cardSize, height = cardSize + 52.dp * scale),
        contentAlignment = Alignment.TopCenter
    ) {
        // Contact shadow — soft horizontal ellipse, no rectangle
        Box(
            Modifier
                .offset(y = cardSize - 8.dp * scale)
                .size(width = cardSize * 0.78f, height = 20.dp * scale)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        paint.asFrameworkPaint().apply {
                            isAntiAlias = true
                            color       = Color.Black.copy(0f).toArgb()
                            maskFilter  = BlurMaskFilter(18f, BlurMaskFilter.Blur.NORMAL)
                        }
                        paint.color = if (theme.isDark)
                            Color.Black.copy(0.28f)
                        else
                            Color(0xFF466482).copy(0.20f)
                        canvas.drawOval(0f, 0f, size.width, size.height, paint)
                    }
                }
        )

        // Subtle reflection — color glow only, no duplicate card image
        Box(
            modifier = Modifier
                .offset(y = cardSize + 4.dp * scale)
                .size(width = cardSize * 0.82f, height = 44.dp * scale)
                .blur(14.dp)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.accent.copy(if (theme.isDark) 0.10f else 0.06f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Glow — wide blue atmospheric halo
        Box(
            Modifier
                .size(cardSize * 1.18f)
                .blur(44.dp)
                .background(
                    Color(0xFF1A72FF).copy(if (theme.isDark) 0.16f else 0.08f),
                    RoundedCornerShape(radius * 1.3f)
                )
        )

        // Glow — tight accent rim
        Box(
            Modifier
                .size(cardSize)
                .blur(16.dp)
                .background(
                    theme.accent.copy(if (theme.isDark) 0.28f else 0.14f),
                    RoundedCornerShape(radius)
                )
        )

        // Card surface
        Box(
            modifier = Modifier
                .size(cardSize)
                .clip(RoundedCornerShape(radius))
                .background(theme.cardBackground)
                .border(1.4.dp, theme.cardBorder, RoundedCornerShape(radius))
        ) {
            // Artwork — padded so logos are not cropped
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model              = station.artworkModel,
                    contentDescription = station.name,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .size(cardSize - 28.dp)
                        .clip(RoundedCornerShape(5.dp))
                )
            }

            // Diagonal glossy highlight — top-left catch light
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0.00f to Color.White.copy(if (theme.isDark) 0.10f else 0.16f),
                                0.42f to Color.White.copy(if (theme.isDark) 0.04f else 0.06f),
                                0.68f to Color.Transparent
                            ),
                            start = Offset.Zero,
                            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
            )

            // Bottom-right dark bevel
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color.Transparent,
                                if (theme.isDark) Color.Black.copy(0.12f)
                                else              Color(0xFF3060A0).copy(0.08f)
                            ),
                            center = Offset(cardSize.value * 1.45f, cardSize.value * 1.45f),
                            radius = cardSize.value * 1.45f
                        )
                    )
            )

            // Equalizer icon when playing
            if (playing) {
                Box(
                    modifier         = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EqualizerBars(
                        bars    = 4,
                        playing = true,
                        color   = theme.accent,
                        height  = 20.dp * scale
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Station info — name, RDS, subtitle
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StationInfo(
    theme:       CarLauncherTheme,
    station:     LauncherStation,
    streamTitle: String?,
    playing:     Boolean,
    scale:       Float,
    modifier:    Modifier = Modifier
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp * scale)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp * scale)
        ) {
            Text(
                text          = station.name,
                color         = theme.primaryText,
                fontSize      = (28 * scale).sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp,
                maxLines      = 1,
                overflow      = TextOverflow.Ellipsis,
                modifier      = Modifier.weight(1f, fill = false)
            )
            if (playing) {
                EqualizerBars(
                    bars    = 4,
                    playing = true,
                    color   = theme.accent,
                    height  = 22.dp * scale
                )
            }
        }

        val displaySubtitle = streamTitle?.takeIf { it.isNotBlank() } ?: station.subtitle
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp * scale)
        ) {
            Icon(
                imageVector        = Icons.Default.MusicNote,
                contentDescription = null,
                tint               = theme.accent,
                modifier           = Modifier.size(18.dp * scale)
            )
            Text(
                text       = displaySubtitle,
                color      = theme.primaryText,
                fontSize   = (18 * scale).sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small station carousel
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SmallStationCarousel(
    theme:    CarLauncherTheme,
    stations: List<LauncherStation>,
    onSelect: (Int) -> Unit,
    scale:    Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp * scale),
        verticalAlignment     = Alignment.Top
    ) {
        stations.take(3).forEachIndexed { index, station ->
            SmallStationCard(
                theme    = theme,
                station  = station,
                cardSize = 92.dp * scale,
                onClick  = { onSelect(index) }
            )
        }
    }
}

@Composable
fun SmallStationCard(
    theme:    CarLauncherTheme,
    station:  LauncherStation,
    cardSize: Dp,
    onClick:  () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Box(
            modifier = Modifier
                .size(cardSize)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    val r = 9.dp.toPx()
                    // Card glow
                    drawIntoCanvas { canvas ->
                        val p = Paint()
                        p.asFrameworkPaint().apply {
                            isAntiAlias = true
                            color       = Color.Black.copy(0f).toArgb()
                            maskFilter  = BlurMaskFilter(w * 0.22f, BlurMaskFilter.Blur.NORMAL)
                        }
                        p.color = theme.accent.copy(if (theme.isDark) 0.22f else 0.12f)
                        canvas.drawRoundRect(-w * 0.04f, -h * 0.04f, w * 1.04f, h * 1.04f, r, r, p)
                    }
                    // Contact shadow
                    drawIntoCanvas { canvas ->
                        val p = Paint()
                        p.asFrameworkPaint().apply {
                            isAntiAlias = true
                            color       = Color.Black.copy(0f).toArgb()
                            maskFilter  = BlurMaskFilter(h * 0.14f, BlurMaskFilter.Blur.NORMAL)
                        }
                        p.color = theme.shadowColor.copy(if (theme.isDark) 0.28f else 0.16f)
                        canvas.drawOval(w * 0.10f, h * 0.88f, w * 0.90f, h * 1.18f, p)
                    }
                }
                .clip(RoundedCornerShape(9.dp))
                .background(theme.cardBackground)
                .border(1.dp, theme.cardBorder, RoundedCornerShape(9.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onClick
                )
        ) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(7.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model              = station.artworkModel,
                    contentDescription = station.name,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5.dp))
                )
            }

            // Gloss overlay
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0.00f to Color.White.copy(if (theme.isDark) 0.14f else 0.22f),
                                0.42f to Color.Transparent
                            ),
                            start = Offset.Zero,
                            end   = Offset(cardSize.value * 1.4f, cardSize.value * 1.4f)
                        )
                    )
            )
        }

        // Subtle reflection — barely visible color glow
        Box(
            modifier = Modifier
                .size(width = cardSize * 0.88f, height = 22.dp)
                .blur(10.dp)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                theme.accent.copy(if (theme.isDark) 0.08f else 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Spacer(Modifier.height(5.dp))
        Text(
            text     = station.name,
            color    = theme.secondaryText,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = cardSize)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Carousel arrow buttons
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CarouselArrowButton(
    direction: Int,
    theme:     CarLauncherTheme,
    onClick:   () -> Unit,
    scale:     Float,
    modifier:  Modifier = Modifier
) {
    val btnSize = 48.dp * scale

    Box(
        modifier = modifier
            .size(btnSize)
            .drawBehind {
                val r = size.minDimension / 2f
                drawIntoCanvas { canvas ->
                    val p = Paint()
                    p.asFrameworkPaint().apply {
                        isAntiAlias = true
                        color       = Color.Black.copy(0f).toArgb()
                        maskFilter  = BlurMaskFilter(r * 0.55f, BlurMaskFilter.Blur.NORMAL)
                    }
                    p.color = theme.shadowColor.copy(0.40f)
                    canvas.drawCircle(Offset(r, r + r * 0.35f), r * 0.65f, p)
                }
            }
            .clip(CircleShape)
            .background(
                if (theme.isDark) Color(0xBD030C18) else Color(0x61FFFFFF)
            )
            .border(
                1.dp,
                if (theme.isDark) Color(0x595AAAF0) else Color(0x470078DC),
                CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = if (direction < 0)
                Icons.AutoMirrored.Filled.KeyboardArrowLeft
            else
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = if (direction < 0) "Предишна" else "Следваща",
            tint               = theme.primaryText,
            modifier           = Modifier.size(28.dp * scale)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top-right status bar — weather, time, settings
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TopStatusBar(
    theme:      CarLauncherTheme,
    onSettings: () -> Unit,
    modifier:   Modifier = Modifier
) {
    Row(
        modifier              = modifier,
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector        = Icons.Default.WbSunny,
            contentDescription = "Weather",
            tint               = if (theme.isDark) Color(0xFFFFC400) else Color(0xFFFFB300),
            modifier           = Modifier.size(24.dp)
        )
        Text(
            text       = "15°",
            color      = theme.primaryText,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text       = "13:36",
            color      = if (theme.isDark) Color(0xFFDCE8F8) else theme.primaryText,
            fontSize   = 21.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector        = Icons.Default.Settings,
            contentDescription = "Settings",
            tint               = theme.iconInactive,
            modifier           = Modifier
                .size(22.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onSettings
                )
        )
    }
}
