package com.bgautoradio.data.model

data class PlaybackState(
    val station: RadioStation?  = null,
    val status: PlaybackStatus  = PlaybackStatus.IDLE,
    val streamTitle: String?    = null,   // ICY metadata title
    val errorMessage: String?   = null,
    val bufferedPercent: Int    = 0
)

enum class PlaybackStatus {
    IDLE,
    LOADING,
    PLAYING,
    PAUSED,
    ERROR,
    RECONNECTING
}
