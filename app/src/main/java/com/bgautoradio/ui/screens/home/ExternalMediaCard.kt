package com.bgautoradio.ui.screens.home

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.repository.ExternalMediaState
import com.bgautoradio.ui.components.AppIcon
import com.bgautoradio.ui.components.EqualizerBars
import com.bgautoradio.ui.theme.*

@Composable
internal fun ExternalMediaCard(
    state:       ExternalMediaState,
    fontScale:   Float    = 1f,
    onPlayPause: () -> Unit = {},
    onSkipNext:  () -> Unit = {},
    onSkipPrev:  () -> Unit = {},
    modifier:    Modifier = Modifier,
) {
    val isDark      = LocalAppColors.current.isDark
    val accentColor = LocalAppColors.current.accent

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val heroSize = (maxHeight * 0.70f).coerceIn(180.dp, 320.dp)
        val nameSize = ((maxHeight.value / 17f).coerceIn(26f, 40f) * fontScale).sp
        val rdsSize  = ((maxHeight.value / 26f).coerceIn(15f, 24f) * fontScale).sp
        val artSize  = ((maxHeight.value / 34f).coerceIn(13f, 20f) * fontScale).sp

        Row(
            modifier          = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, top = 72.dp, bottom = 20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ── LEFT: album art glass card ────────────────────────────────────
            AlbumArtHeroCard(
                state    = state,
                cardSize = heroSize,
                modifier = Modifier.weight(0.42f)
            )

            Spacer(Modifier.width(36.dp))

            // ── RIGHT: title + artist + app info ──────────────────────────────
            Column(
                modifier            = Modifier
                    .weight(0.58f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // App label + equalizer
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppIcon(packageName = state.packageName, size = 28)
                        Text(
                            text       = state.appLabel,
                            color      = TextSecondary,
                            fontSize   = artSize,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.weight(1f, fill = false)
                        )
                        EqualizerBars(bars = 4, playing = state.isPlaying, color = accentColor, height = 22.dp)
                    }

                    Spacer(Modifier.height(4.dp))

                    // Song title
                    if (state.title != null) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.MusicNote, null, tint = Accent, modifier = Modifier.size(20.dp))
                            Text(
                                text       = state.title,
                                color      = TextPrimary,
                                fontSize   = rdsSize,
                                fontWeight = FontWeight.SemiBold,
                                maxLines   = 2,
                                overflow   = TextOverflow.Ellipsis,
                                lineHeight  = (rdsSize.value * 1.25f).sp,
                            )
                        }
                    }

                    // Artist
                    if (state.artist != null) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Person, null, tint = TextTertiary, modifier = Modifier.size(17.dp))
                            Text(
                                text     = state.artist,
                                color    = TextSecondary,
                                fontSize = artSize,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Transport controls
                Row(
                    modifier              = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    MediaControlButton(icon = Icons.Default.SkipPrevious, size = 48.dp, onClick = onSkipPrev)
                    MediaControlButton(
                        icon    = if (state.isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                        size    = 64.dp,
                        onClick = onPlayPause,
                        accent  = true,
                    )
                    MediaControlButton(icon = Icons.Default.SkipNext, size = 48.dp, onClick = onSkipNext)
                }

                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AlbumArtHeroCard(
    state:    ExternalMediaState,
    cardSize: Dp,
    modifier: Modifier = Modifier,
) {
    val isDark      = LocalAppColors.current.isDark
    val accentColor = LocalAppColors.current.accent
    val radius      = 22.dp

    val glowTr = rememberInfiniteTransition(label = "eg")
    val glowA by glowTr.animateFloat(
        initialValue  = 0.72f,
        targetValue   = 1.00f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "ga"
    )

    val glassBg = if (isDark)
        Brush.linearGradient(
            listOf(
                Color.White.copy(0.10f),
                Color(0xFF081A30).copy(0.68f),
                Color.Black.copy(0.82f),
            ),
            start = Offset.Zero,
            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    else
        Brush.linearGradient(listOf(Color.White.copy(0.94f), Color(0xFFCBD8F2)))

    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier.size(width = cardSize, height = cardSize + 88.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Card body
            Box(
                modifier = Modifier
                    .size(cardSize)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        val r = radius.toPx()

                        // Contact shadow
                        drawIntoCanvas { canvas ->
                            val p = Paint(); val fp = p.asFrameworkPaint()
                            fp.isAntiAlias = true
                            fp.color      = Color.Black.copy(0f).toArgb()
                            fp.maskFilter = BlurMaskFilter(22.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                            p.color       = Color.Black.copy(0.55f)
                            canvas.drawOval(w * 0.09f, h * 0.86f, w * 0.91f, h * 1.20f, p)
                        }

                        // Blue atmospheric halo
                        drawIntoCanvas { canvas ->
                            val p = Paint(); val fp = p.asFrameworkPaint()
                            fp.isAntiAlias = true
                            fp.color      = Color(0xFF1A72FF).copy(0f).toArgb()
                            fp.maskFilter = BlurMaskFilter(52.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                            p.color       = Color(0xFF1A72FF).copy(0.20f * glowA)
                            canvas.drawRoundRect(-w * 0.10f, -h * 0.10f, w * 1.10f, h * 1.10f, r * 1.4f, r * 1.4f, p)
                        }

                        // Cyan card glow
                        drawIntoCanvas { canvas ->
                            val p = Paint(); val fp = p.asFrameworkPaint()
                            fp.isAntiAlias = true
                            fp.color      = accentColor.copy(0f).toArgb()
                            fp.maskFilter = BlurMaskFilter(36.dp.toPx(), BlurMaskFilter.Blur.NORMAL)
                            p.color       = accentColor.copy(0.28f * glowA)
                            canvas.drawRoundRect(-w * 0.06f, -h * 0.06f, w * 1.06f, h * 1.06f, r * 1.2f, r * 1.2f, p)
                        }
                    }
                    .clip(RoundedCornerShape(radius))
                    .background(glassBg)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                accentColor.copy(0.85f * glowA),
                                accentColor.copy(0.30f),
                                Color.White.copy(0.12f),
                            )
                        ),
                        shape = RoundedCornerShape(radius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (state.albumArt != null) {
                    androidx.compose.foundation.Image(
                        bitmap             = state.albumArt.asImageBitmap(),
                        contentDescription = state.title,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(radius))
                    )
                } else {
                    AppIcon(packageName = state.packageName, size = (cardSize.value * 0.55f).toInt())
                }

                // Gloss overlay (diagonal catch-light)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(radius))
                        .background(
                            Brush.linearGradient(
                                0.0f to Color.White.copy(if (isDark) 0.08f else 0.25f),
                                0.5f to Color.Transparent,
                                start = Offset.Zero,
                                end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        )
                )

                // Inner bevel — top white highlight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(topStart = radius, topEnd = radius))
                        .background(Color.White.copy(if (isDark) 0.22f else 0.55f))
                )
            }
        }
    }
}

@Composable
private fun MediaControlButton(
    icon:    androidx.compose.ui.graphics.vector.ImageVector,
    size:    Dp,
    onClick: () -> Unit,
    accent:  Boolean = false,
) {
    val accentColor = LocalAppColors.current.accent
    val tint = if (accent) accentColor else TextSecondary
    Icon(
        imageVector        = icon,
        contentDescription = null,
        tint               = tint,
        modifier           = Modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick,
            )
    )
}
