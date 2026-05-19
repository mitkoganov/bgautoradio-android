package com.bgautoradio.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.bgautoradio.data.repository.WazeAlert
import com.bgautoradio.data.repository.WazeAlertRepository
import com.bgautoradio.data.repository.WazeAlertType

class WazeAccessibilityService : AccessibilityService() {

    private var lastAlertKey  = ""
    private var lastAlertTime = 0L

    override fun onServiceConnected() {
        serviceInfo = serviceInfo.apply {
            eventTypes    = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames  = arrayOf("com.waze")
            feedbackType  = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 300
            flags         = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                            AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        Log.d("WazeA11y", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName != "com.waze") return
        val now = System.currentTimeMillis()
        // Throttle: don't re-scan more often than every 1.5s
        if (now - lastAlertTime < 1500) return

        val root = rootInActiveWindow ?: return
        val texts = mutableListOf<String>()
        collectTexts(root, texts)
        root.recycle()

        val combined = texts.joinToString(" ").lowercase()
        if (combined.isBlank()) return

        val alert = parseTexts(combined) ?: run {
            // No alert in current screen — clear if we had one
            if (lastAlertKey.isNotEmpty() && now - lastAlertTime > 10_000) {
                lastAlertKey = ""
                WazeAlertRepository.clear()
            }
            return
        }

        // Deduplicate: same type+distance → don't re-post
        val key = "${alert.type}|${alert.text}"
        if (key == lastAlertKey) return

        lastAlertKey  = key
        lastAlertTime = now
        Log.d("WazeA11y", "Alert: $key")
        WazeAlertRepository.post(alert)
    }

    private fun collectTexts(node: AccessibilityNodeInfo, out: MutableList<String>) {
        node.text?.toString()?.takeIf { it.isNotBlank() }?.let { out.add(it) }
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { out.add(it) }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                collectTexts(child, out)
                child.recycle()
            }
        }
    }

    private fun parseTexts(text: String): WazeAlert? = when {
        text.contains("police") || text.contains("полиц") || text.contains("cop") ->
            WazeAlert(WazeAlertType.POLICE, buildLabel(text, "police|полиц|cop", "Полиция напред"))

        text.contains("camera") || text.contains("камер") || text.contains("speed cam") ->
            WazeAlert(WazeAlertType.POLICE, buildLabel(text, "camera|камер|speed cam", "Камера напред"))

        text.contains("accident") || text.contains("crash") ||
        text.contains("инцидент") || text.contains("катастроф") ->
            WazeAlert(WazeAlertType.ACCIDENT, buildLabel(text, "accident|crash|инцидент|катастроф", "Инцидент напред"))

        text.contains("hazard") || text.contains("опасност") || text.contains("debris") ->
            WazeAlert(WazeAlertType.HAZARD, buildLabel(text, "hazard|опасност|debris", "Опасност напред"))

        text.contains("traffic") || text.contains("jam") || text.contains("задръств") ->
            WazeAlert(WazeAlertType.TRAFFIC, buildLabel(text, "traffic|jam|задръств", "Задръстване"))

        else -> null
    }

    // Find "X in 40 m" or "X in 1.2 km" pattern near the keyword
    private fun buildLabel(text: String, keyPattern: String, fallback: String): String {
        val dist = Regex("(?:$keyPattern)\\s+(?:in\\s+)?(\\d+\\.?\\d*\\s*(?:km|m)\\b)", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.get(1)?.trim()
            ?: Regex("(\\d+\\.?\\d*\\s*(?:km|m)\\b)", RegexOption.IGNORE_CASE).find(text)?.value
        return if (!dist.isNullOrBlank()) "$fallback · $dist" else fallback
    }

    override fun onInterrupt() {}
}
