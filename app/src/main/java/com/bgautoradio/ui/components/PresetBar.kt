package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.ui.theme.*

/**
 * Horizontal preset bar — row of quick-access station buttons.
 * OEM-style: numbered labels (P1–P8), station name, logo thumbnail.
 */
@Composable
fun PresetBar(
    presets:        List<RadioStation>,
    currentStation: RadioStation?,
    onPresetClick:  (RadioStation) -> Unit,
    modifier:       Modifier = Modifier
) {
    LazyRow(
        modifier              = modifier
            .fillMaxWidth()
            .background(DarkNavy)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        items(
            items = presets.take(8),
            key   = { it.id }
        ) { station ->
            PresetButton(
                index          = presets.indexOf(station) + 1,
                station        = station,
                isActive       = station.id == currentStation?.id,
                onClick        = { onPresetClick(station) }
            )
        }
    }
}

@Composable
private fun PresetButton(
    index:    Int,
    station:  RadioStation,
    isActive: Boolean,
    onClick:  () -> Unit
) {
    val bgColor     = if (isActive) AccentCyanGlow else CardSurface
    val borderColor = if (isActive) AccentCyan else BorderSubtle
    val textColor   = if (isActive) AccentCyan else TextPrimary

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        // Preset number — top left
        Text(
            text      = "P$index",
            color     = if (isActive) AccentCyan else TextSecondary,
            fontSize  = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier  = Modifier.align(Alignment.TopStart)
        )

        // Station name — center
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text      = station.name,
                color     = textColor,
                fontSize  = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style     = MaterialTheme.typography.labelMedium.copy(color = textColor)
            )
        }

        // Category emoji — bottom right
        Text(
            text     = station.category.emoji,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
