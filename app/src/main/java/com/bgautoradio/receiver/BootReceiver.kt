package com.bgautoradio.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bgautoradio.data.preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var prefs: AppPreferences

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Always restart after OTA update
                context.packageManager
                    .getLaunchIntentForPackage(context.packageName)
                    ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP) }
                    ?.let { context.startActivity(it) }
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val autoStart = prefs.autoPlayOnBoot.first()
                    if (autoStart) {
                        context.packageManager
                            .getLaunchIntentForPackage(context.packageName)
                            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                            ?.let { context.startActivity(it) }
                    }
                }
            }
        }
    }
}
