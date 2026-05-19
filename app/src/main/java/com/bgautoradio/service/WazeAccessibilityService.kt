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
        val root = rootInActiveWindow ?: return
        val texts = mutableListOf<String>()
        collectTexts(root, texts)
        root.recycle()

        val combined = texts.joinToString(" ").lowercase()
        if (combined.isBlank()) return

        Log.d("WazeA11y", "texts: $combined")

        val alert = parseTexts(combined) ?: return
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
        text.contains("police") || text.contains("полиц") ||
        text.contains("cop") || text.contains("камер") ||
        text.contains("camera") || text.contains("speed cam") ->
            WazeAlert(WazeAlertType.POLICE, buildLabel(text, "Полиция/Камера напред"))

        text.contains("accident") || text.contains("crash") ||
        text.contains("инцидент") || text.contains("катастроф") ->
            WazeAlert(WazeAlertType.ACCIDENT, buildLabel(text, "Инцидент напред"))

        text.contains("hazard") || text.contains("опасност") ||
        text.contains("debris") || text.contains("object") ->
            WazeAlert(WazeAlertType.HAZARD, buildLabel(text, "Опасност напред"))

        text.contains("traffic") || text.contains("jam") ||
        text.contains("задръств") || text.contains("slow") ->
            WazeAlert(WazeAlertType.TRAFFIC, buildLabel(text, "Задръстване"))

        else -> null
    }

    // Extract distance hint like "200m" or "1.2km" from text if present
    private fun buildLabel(text: String, fallback: String): String {
        val dist = Regex("(\\d+\\.?\\d*\\s*(km|m))", RegexOption.IGNORE_CASE)
            .find(text)?.value
        return if (dist != null) "$fallback · $dist" else fallback
    }

    override fun onInterrupt() {}
}
