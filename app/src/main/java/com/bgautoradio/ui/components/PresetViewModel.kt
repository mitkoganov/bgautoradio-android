package com.bgautoradio.ui.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class AppInfo(val packageName: String, val appName: String)

@HiltViewModel
class PresetViewModel @Inject constructor(
    private val prefs: AppPreferences,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    data class PresetState(
        val preset1:  AppInfo?       = null,
        val preset2:  AppInfo?       = null,
        val allApps:  List<AppInfo>  = emptyList(),
    )

    val state: StateFlow<PresetState> = combine(
        prefs.preset1Id,
        prefs.preset2Id,
    ) { id1, id2 ->
        id1 to id2
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null to null)
        .let { flow ->
            combine(flow, kotlinx.coroutines.flow.flow {
                emit(loadInstalledApps())
            }) { (id1, id2), apps ->
                PresetState(
                    preset1  = apps.firstOrNull { it.packageName == id1 },
                    preset2  = apps.firstOrNull { it.packageName == id2 },
                    allApps  = apps,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PresetState())
        }

    private suspend fun loadInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .filter { it.packageName != context.packageName }
            .map { AppInfo(packageName = it.packageName, appName = it.loadLabel(pm).toString()) }
            .sortedBy { it.appName }
    }

    fun setPreset(slot: Int, app: AppInfo) = viewModelScope.launch {
        if (slot == 1) prefs.setPreset1Id(app.packageName)
        else           prefs.setPreset2Id(app.packageName)
    }

    fun launchPreset(slot: Int) {
        val pkg = if (slot == 1) state.value.preset1?.packageName
                  else           state.value.preset2?.packageName
        pkg?.let {
            val intent = context.packageManager.getLaunchIntentForPackage(it)
                ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            if (intent != null) context.startActivity(intent)
        }
    }
}
