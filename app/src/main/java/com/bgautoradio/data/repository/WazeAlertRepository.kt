package com.bgautoradio.data.repository

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class WazeAlertType(val emoji: String, val label: String) {
    POLICE   ("🚔", "Полиция"),
    ACCIDENT ("💥", "Инцидент"),
    HAZARD   ("⚠️", "Опасност"),
    TRAFFIC  ("🚗", "Задръстване"),
    OTHER    ("📍", "Събитие"),
}

data class WazeAlert(val type: WazeAlertType, val text: String)

object WazeAlertRepository {
    private val _alert = MutableStateFlow<WazeAlert?>(null)
    val alert: StateFlow<WazeAlert?> = _alert.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var clearJob: Job? = null

    fun post(alert: WazeAlert) {
        _alert.value = alert
        clearJob?.cancel()
        clearJob = scope.launch {
            delay(20_000)
            _alert.value = null
        }
    }

    fun clear() { _alert.value = null; clearJob?.cancel() }

    fun parseNotification(title: String?, text: String?): WazeAlert? {
        val combined = "${title.orEmpty()} ${text.orEmpty()}".lowercase()
        return when {
            combined.contains("полиц") || combined.contains("police") || combined.contains("камера") ->
                WazeAlert(WazeAlertType.POLICE, text ?: "Полиция напред")
            combined.contains("инцидент") || combined.contains("accident") || combined.contains("crash") ->
                WazeAlert(WazeAlertType.ACCIDENT, text ?: "Инцидент")
            combined.contains("опасност") || combined.contains("hazard") ->
                WazeAlert(WazeAlertType.HAZARD, text ?: "Опасност")
            combined.contains("задръстване") || combined.contains("traffic") || combined.contains("jam") ->
                WazeAlert(WazeAlertType.TRAFFIC, text ?: "Задръстване")
            combined.contains("waze") && combined.isNotBlank() ->
                WazeAlert(WazeAlertType.OTHER, text ?: title ?: "Събитие")
            else -> null
        }
    }
}
