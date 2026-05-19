package com.bgautoradio.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.bgautoradio.data.repository.ExternalMediaRepository
import com.bgautoradio.data.repository.WazeAlertRepository
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class WazeNotificationService : NotificationListenerService() {

    @Inject lateinit var externalMediaRepository: ExternalMediaRepository

    override fun onListenerConnected() {
        super.onListenerConnected()
        externalMediaRepository.startListening()
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        externalMediaRepository.stopListening()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "com.waze") return
        val extras   = sbn.notification.extras
        val title    = extras.getString("android.title")
        val text     = extras.getCharSequence("android.text")?.toString()
        val bigText  = extras.getCharSequence("android.bigText")?.toString()
        val subText  = extras.getCharSequence("android.subText")?.toString()
        val category = sbn.notification.category

        // Log everything so we know what Waze actually sends
        Log.d("WazeNotif", "=== WAZE NOTIFICATION ===")
        Log.d("WazeNotif", "title   = $title")
        Log.d("WazeNotif", "text    = $text")

        val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val sb = StringBuilder()
        sb.appendLine("=== $ts ===")
        sb.appendLine("title    = $title")
        sb.appendLine("text     = $text")
        sb.appendLine("bigText  = $bigText")
        sb.appendLine("subText  = $subText")
        sb.appendLine("category = $category")
        sb.appendLine("extras keys: ${extras.keySet()}")
        extras.keySet().forEach { key ->
            sb.appendLine("  [$key] = ${extras.get(key)}")
        }
        sb.appendLine()
        writeLog(sb.toString())

        val alert = WazeAlertRepository.parseNotification(title, text) ?: return
        WazeAlertRepository.post(alert)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == "com.waze") WazeAlertRepository.clear()
    }

    private fun writeLog(text: String) {
        try {
            val dir = getExternalFilesDir(null) ?: filesDir
            File(dir, "waze_log.txt").appendText(text)
        } catch (e: Exception) {
            Log.e("WazeNotif", "writeLog failed", e)
        }
    }
}
