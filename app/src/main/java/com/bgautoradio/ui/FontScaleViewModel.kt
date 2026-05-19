package com.bgautoradio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FontScaleViewModel @Inject constructor(private val prefs: AppPreferences) : ViewModel() {
    val topBarScale   = prefs.fontScaleTopBar.stateIn(viewModelScope, SharingStarted.Eagerly, 1f)
    val railScale     = prefs.fontScaleRail.stateIn(viewModelScope, SharingStarted.Eagerly, 1f)
    val carouselScale = prefs.fontScaleCarousel.stateIn(viewModelScope, SharingStarted.Eagerly, 1f)
}
