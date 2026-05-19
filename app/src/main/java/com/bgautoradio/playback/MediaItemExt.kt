package com.bgautoradio.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.bgautoradio.data.model.RadioStation

@OptIn(UnstableApi::class)
fun RadioStation.toMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id)
    .setUri(streamUrl)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(name)
            .setArtist(city ?: category.displayName)
            .setArtworkUri(logoUrl?.let { Uri.parse(it) })
            .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
            .setIsPlayable(true)
            .setIsBrowsable(false)
            .build()
    )
    .build()

@OptIn(UnstableApi::class)
fun RadioStation.toBrowsableMediaItem(): MediaItem = MediaItem.Builder()
    .setMediaId(id)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(name)
            .setArtist(city ?: category.displayName)
            .setArtworkUri(logoUrl?.let { Uri.parse(it) })
            .setMediaType(MediaMetadata.MEDIA_TYPE_RADIO_STATION)
            .setIsPlayable(true)
            .setIsBrowsable(false)
            .build()
    )
    .build()
