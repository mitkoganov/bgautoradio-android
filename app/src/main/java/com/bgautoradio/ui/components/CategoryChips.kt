package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.model.RadioCategory
import com.bgautoradio.ui.theme.*

@Composable
fun CategoryChips(
    selected:  RadioCategory?,
    onSelect:  (RadioCategory?) -> Unit,
    modifier:  Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // "Всички" chip
        CategoryChip(
            label      = "🎵 Всички",
            isSelected = selected == null,
            onClick    = { onSelect(null) }
        )
        RadioCategory.entries.forEach { category ->
            CategoryChip(
                label      = "${category.emoji} ${category.displayName}",
                isSelected = selected == category,
                onClick    = { onSelect(if (selected == category) null else category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    label:      String,
    isSelected: Boolean,
    onClick:    () -> Unit
) {
    val bg      = if (isSelected) AccentCyanGlow else CardSurface
    val border  = if (isSelected) AccentCyan else BorderSubtle
    val textClr = if (isSelected) AccentCyan else TextSecondary

    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text      = label,
            color     = textClr,
            fontSize  = 12.sp
        )
    }
}
