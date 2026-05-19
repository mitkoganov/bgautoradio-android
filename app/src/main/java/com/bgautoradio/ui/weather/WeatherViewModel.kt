package com.bgautoradio.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.model.WeatherData
import com.bgautoradio.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherData?>(null)
    val weather = _weather.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _weather.value = repository.fetchWeather()
                delay(10 * 60 * 1_000L)
            }
        }
    }
}
