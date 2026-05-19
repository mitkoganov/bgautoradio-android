package com.bgautoradio.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bgautoradio.data.repository.WazeAlert
import com.bgautoradio.data.repository.WazeAlertRepository

class WazeTestReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: "police"
        val text = intent.getStringExtra("text") ?: "Тест алерт"
        val alert = WazeAlertRepository.parseNotification(type, text)
            ?: WazeAlert(com.bgautoradio.data.repository.WazeAlertType.POLICE, text)
        WazeAlertRepository.post(alert)
    }
}
