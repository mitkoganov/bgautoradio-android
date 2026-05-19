package com.bgautoradio

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.data.preferences.AppPreferences
import com.bgautoradio.data.repository.SpotifyRepository
import com.bgautoradio.ui.navigation.AppNavigation
import com.bgautoradio.ui.overlay.FloatingRailService
import com.bgautoradio.ui.theme.AutoRadioTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var prefs: AppPreferences
    @Inject lateinit var spotifyRepository: SpotifyRepository

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* weather widget handles missing permission gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        locationPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ))

        enableEdgeToEdge()
        setupImmersiveMode()

        setContent {
            val themeMode  by prefs.themeMode.collectAsStateWithLifecycle(initialValue = "auto")
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "day"   -> false
                "night" -> true
                else    -> systemDark
            }

            AutoRadioTheme(isDark = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, FloatingRailService::class.java))
        restartIfOtaInstalled()
    }

    // Restart once per installed version if old process is still running after OTA
    private fun restartIfOtaInstalled() {
        val installedCode = packageManager
            .getPackageInfo(packageName, 0).longVersionCode.toInt()
        if (installedCode <= BuildConfig.VERSION_CODE) return

        val prefs = getSharedPreferences("ota", MODE_PRIVATE)
        val alreadyTriedFor = prefs.getInt("restart_for_version", 0)
        if (alreadyTriedFor >= installedCode) return  // don't loop

        prefs.edit().putInt("restart_for_version", installedCode).apply()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    // Handles OAuth PKCE callback: bgautoradio://spotify-callback?code=...
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri  = intent.data ?: return
        if (uri.scheme == "bgautoradio" && uri.host == "spotify-callback") {
            val code = uri.getQueryParameter("code") ?: return
            spotifyRepository.setPendingCode(code)
        }
    }

    private fun setupImmersiveMode() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        // Hide only status bar — keep nav bar so Home button remains accessible
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}
