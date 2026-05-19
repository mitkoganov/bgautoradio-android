package com.bgautoradio.ui.screens.settings

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgautoradio.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

data class OtaState(
    val checking:        Boolean = false,
    val downloading:     Boolean = false,
    val progress:        Int     = 0,
    val updateAvailable: Boolean = false,
    val latestVersion:   String? = null,
    val downloadUrl:     String? = null,
    val changelog:       String? = null,
    val error:           String? = null,
    val readyToInstall:  Boolean = false,
    val apkPath:         String? = null,
)

@HiltViewModel
class OtaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
) : ViewModel() {

    private val _state = MutableStateFlow(OtaState())
    val state: StateFlow<OtaState> = _state.asStateFlow()

    private val githubOwner = "mitkoganov"
    private val githubRepo  = "bgautoradio-android"
    private val apiUrl      = "https://api.github.com/repos/$githubOwner/$githubRepo/releases/latest"

    fun checkForUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(checking = true, error = null, updateAvailable = false)
            runCatching {
                val body = okHttpClient.newCall(
                    Request.Builder()
                        .url(apiUrl)
                        .header("Accept", "application/vnd.github+json")
                        .build()
                ).execute().body?.string() ?: throw Exception("Няма отговор от сървъра")

                val json      = JSONObject(body)
                val tagName   = json.getString("tag_name")
                val changelog = json.optString("body", "").take(300)

                val assets = json.getJSONArray("assets")
                val apkUrl = (0 until assets.length())
                    .map { assets.getJSONObject(it) }
                    .firstOrNull { it.getString("name").endsWith(".apk") }
                    ?.getString("browser_download_url")

                val isNewer = compareVersions(tagName, "v${BuildConfig.VERSION_NAME}") > 0

                if (isNewer && apkUrl != null) {
                    _state.value = _state.value.copy(
                        checking        = false,
                        updateAvailable = true,
                        latestVersion   = tagName,
                        downloadUrl     = apkUrl,
                        changelog       = changelog,
                    )
                } else {
                    _state.value = _state.value.copy(
                        checking        = false,
                        updateAvailable = false,
                        latestVersion   = tagName,
                    )
                }
            }.onFailure { e ->
                _state.value = _state.value.copy(
                    checking = false,
                    error    = e.message ?: "Грешка при проверка",
                )
            }
        }
    }

    fun downloadAndInstall() {
        val url = _state.value.downloadUrl ?: return
        _state.value = _state.value.copy(downloading = true, progress = 0, error = null)

        val apkFile = File(context.getExternalFilesDir(null), "update.apk")
        if (apkFile.exists()) apkFile.delete()

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("BG Auto Radio — обновление")
            .setDescription(_state.value.latestVersion)
            .setDestinationUri(Uri.fromFile(apkFile))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        val downloadId = dm.enqueue(request)

        viewModelScope.launch(Dispatchers.IO) {
            var downloading = true
            while (downloading) {
                val cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        when (status) {
                            DownloadManager.STATUS_RUNNING -> {
                                val total      = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                val downloaded = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                val pct = if (total > 0) (downloaded * 100 / total).toInt() else 0
                                _state.value = _state.value.copy(progress = pct)
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                _state.value = _state.value.copy(
                                    downloading    = false,
                                    progress       = 100,
                                    readyToInstall = true,
                                    apkPath        = apkFile.absolutePath,
                                )
                                downloading = false
                            }
                            DownloadManager.STATUS_FAILED -> {
                                _state.value = _state.value.copy(
                                    downloading = false,
                                    error       = "Изтеглянето се провали",
                                )
                                downloading = false
                            }
                        }
                    }
                }
                if (downloading) kotlinx.coroutines.delay(500)
            }
        }
    }

    fun installApk() {
        val path = _state.value.apkPath ?: return
        val file = File(path)
        if (!file.exists()) return
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun compareVersions(a: String, b: String): Int {
        val pa = a.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        val pb = b.removePrefix("v").split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(pa.size, pb.size)) {
            val diff = pa.getOrElse(i) { 0 } - pb.getOrElse(i) { 0 }
            if (diff != 0) return diff
        }
        return 0
    }
}
