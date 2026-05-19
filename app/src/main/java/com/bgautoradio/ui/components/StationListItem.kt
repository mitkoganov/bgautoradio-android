package com.bgautoradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bgautoradio.data.model.RadioStation
import com.bgautoradio.ui.theme.*

@Composable
fun StationListItem(
    station:          RadioStation,
    isPlaying:        Boolean      = false,
    onClick:          () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier:         Modifier     = Modifier
) {
    val bgColor = when {
        isPlaying -> AccentCyanGlow
        else      -> CardSurface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(horizontal = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Thumbnail logo
        StationLogo(
            station      = station,
            size         = 48.dp,
            cornerRadius = 8.dp
        )

        // Name + metadata
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text      = station.name,
                style     = MaterialTheme.typography.headlineSmall.copy(
                    color    = if (isPlaying) AccentCyan else TextPrimary,
                    fontSize = 15.sp
                ),
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "${station.category.emoji} ${station.category.displayName}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp),
                    maxLines = 1
                )
                if (!station.city.isNullOrBlank()) {
                    Text(
                        text  = "· ${station.city}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                    )
                }
                if (station.bitrate != null) {
                    Text(
                        text  = "${station.bitrate}k",
                        style = MaterialTheme.typography.labelSmall.copy(color = AccentCyanDim, fontSize = 10.sp)
                    )
                }
            }
        }

        // No stream badge
        if (!station.hasValidStream) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Без stream URL",
                tint   = StatusAmber,
                modifier = Modifier.size(18.dp)
            )
        }

        // Playing indicator
        if (isPlaying) {
            Icon(
                Icons.Default.PlayCircle,
                contentDescription = "Свири",
                tint   = AccentCyan,
                modifier = Modifier.size(22.dp)
            )
        }

        // Favorite button
        IconButton(
            onClick = onFavoriteToggle,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (station.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Любими",
                tint   = if (station.isFavorite) AccentAmber else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
