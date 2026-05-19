package com.bgautoradio.data.model

data class SpotifyPlaylist(
    val id:         String,
    val name:       String,
    val imageUrl:   String?,
    val trackCount: Int,
    val uri:        String,
)
