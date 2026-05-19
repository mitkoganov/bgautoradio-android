package com.bgautoradio.data.model

data class SpotifyTrack(
    val id:         String,
    val title:      String,
    val artist:     String,
    val albumArt:   String?,
    val durationMs: Long,
    val uri:        String,
)
