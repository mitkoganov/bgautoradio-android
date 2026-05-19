package com.bgautoradio.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.preferences.AppPreferences
import com.bgautoradio.data.repository.RadioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val autoPlayOnStart:  Boolean = true,
    val autoPlayOnBoot:   Boolean = false,
    val keepScreenAwake:  Boolean = true,
    val mobileDataWarning: Boolean = true,
    val themeMode:        String  = "auto",
    val startScreen:      String  = "home",
    val catalogVersion:   Int     = 0,
    val catalogUpdatedAt: String? = null,
    val isRefreshing:     Boolean = false,
    val refreshMessage:   String? = null,
    val fontScaleTopBar:   Float  = 1.0f,
    val fontScaleRail:     Float  = 1.0f,
    val fontScaleCarousel: Float  = 1.0f,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs:      AppPreferences,
    private val repository: RadioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                prefs.autoPlayOnStart,
                prefs.autoPlayOnBoot,
                prefs.keepScreenAwake,
                prefs.mobileDataWarning,
                prefs.themeMode
            ) { autoStart, autoBoot, keepAwake, mobileWarn, theme ->
                SettingsUiState(
                    autoPlayOnStart   = autoStart,
                    autoPlayOnBoot    = autoBoot,
                    keepScreenAwake   = keepAwake,
                    mobileDataWarning = mobileWarn,
                    themeMode         = theme
                )
            }.combine(prefs.startScreen) { state, screen ->
                state.copy(startScreen = screen)
            }.combine(prefs.catalogVersion) { state, version ->
                state.copy(catalogVersion = version)
            }.combine(prefs.catalogUpdatedAt) { state, updatedAt ->
                state.copy(catalogUpdatedAt = updatedAt)
            }.combine(prefs.fontScaleTopBar) { state, scale ->
                state.copy(fontScaleTopBar = scale)
            }.combine(prefs.fontScaleRail) { state, scale ->
                state.copy(fontScaleRail = scale)
            }.combine(prefs.fontScaleCarousel) { state, scale ->
                state.copy(fontScaleCarousel = scale)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setAutoPlayOnStart(v: Boolean)  = viewModelScope.launch { prefs.setAutoPlayOnStart(v) }
    fun setAutoPlayOnBoot(v: Boolean)   = viewModelScope.launch { prefs.setAutoPlayOnBoot(v) }
    fun setKeepScreenAwake(v: Boolean)  = viewModelScope.launch { prefs.setKeepScreenAwake(v) }
    fun setMobileDataWarning(v: Boolean) = viewModelScope.launch { prefs.setMobileDataWarning(v) }
    fun setThemeMode(v: String)         = viewModelScope.launch { prefs.setThemeMode(v) }
    fun setStartScreen(v: String)       = viewModelScope.launch { prefs.setStartScreen(v) }
    fun setFontScaleTopBar(v: Float)   = viewModelScope.launch { prefs.setFontScaleTopBar(v) }
    fun setFontScaleRail(v: Float)     = viewModelScope.launch { prefs.setFontScaleRail(v) }
    fun setFontScaleCarousel(v: Float) = viewModelScope.launch { prefs.setFontScaleCarousel(v) }

    fun refreshCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, refreshMessage = null) }
            val result = repository.refreshFromRemote()
            _uiState.update { it.copy(
                isRefreshing  = false,
                refreshMessage = if (result.isSuccess) "✓ Каталогът е актуализиран" else "✗ Грешка при актуализация"
            )}
        }
    }
}
