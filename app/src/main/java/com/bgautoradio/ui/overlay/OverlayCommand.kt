package com.bgautoradio.ui.overlay

import kotlinx.coroutines.flow.MutableSharedFlow

object OverlayCommand {
    val navigateTo = MutableSharedFlow<String>(extraBufferCapacity = 1)
}
