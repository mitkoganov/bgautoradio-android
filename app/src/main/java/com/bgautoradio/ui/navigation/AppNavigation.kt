package com.bgautoradio.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.ui.overlay.OverlayCommand
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.ui.FontScaleViewModel
import com.bgautoradio.ui.components.AutomotiveRail
import com.bgautoradio.ui.components.PresetViewModel
import com.bgautoradio.ui.components.AppPickerDialog
import com.bgautoradio.ui.theme.Bg0
import com.bgautoradio.ui.components.TopRightOverlay
import com.bgautoradio.ui.components.SpeedWidget
import com.bgautoradio.ui.launcher.BackgroundImageLayer
import com.bgautoradio.ui.launcher.darkCarTheme
import com.bgautoradio.ui.launcher.lightCarTheme
import com.bgautoradio.ui.theme.LocalAppColors
import com.bgautoradio.ui.screens.categories.CategoriesScreen
import com.bgautoradio.ui.screens.home.HomeScreen
import com.bgautoradio.ui.screens.recent.RecentScreen
import com.bgautoradio.ui.screens.search.SearchScreen
import com.bgautoradio.ui.screens.settings.SettingsScreen
import com.bgautoradio.ui.screens.spotify.SpotifyScreen
import com.bgautoradio.ui.screens.stations.StationsScreen
import com.bgautoradio.ui.screens.apps.AppsScreen
import com.bgautoradio.ui.screens.apps.RailEdgePx
sealed class NavRoute(val route: String) {
    object Home       : NavRoute("home")
    object Channels   : NavRoute("channels")
    object Spotify    : NavRoute("spotify")
    object Categories : NavRoute("categories")
    object Search     : NavRoute("search")
    object Recent     : NavRoute("recent")
    object Settings   : NavRoute("settings")
    object Apps       : NavRoute("apps")
}

@Composable
fun AppNavigation() {
    val navController  = rememberNavController()
    val backStack      by navController.currentBackStackEntryAsState()
    val currentRoute   = backStack?.destination?.route ?: NavRoute.Home.route

    val fontVm: FontScaleViewModel = hiltViewModel()
    val topBarScale by fontVm.topBarScale.collectAsStateWithLifecycle()
    val railScale   by fontVm.railScale.collectAsStateWithLifecycle()

    val presetVm: PresetViewModel = hiltViewModel()
    val presetState by presetVm.state.collectAsStateWithLifecycle()
    var pickerSlot by remember { mutableStateOf(0) }

    fun navigate(route: String) {
        if (route == NavRoute.Home.route) {
            navController.popBackStack(NavRoute.Home.route, inclusive = false)
            return
        }
        if (route == currentRoute) return
        navController.navigate(route) {
            popUpTo(NavRoute.Home.route) { saveState = false }
            launchSingleTop = true
            restoreState    = false
        }
    }

    // Navigate from overlay rail
    LaunchedEffect(Unit) {
        OverlayCommand.navigateTo.collect { route -> navigate(route) }
    }


    val isDark = LocalAppColors.current.isDark

    Box(modifier = Modifier.fillMaxSize().background(Bg0)) {

        val showBackground = currentRoute in setOf(
            NavRoute.Home.route, NavRoute.Channels.route,
            NavRoute.Spotify.route, NavRoute.Apps.route
        )
        if (showBackground) {
            BackgroundImageLayer(if (isDark) darkCarTheme() else lightCarTheme())
        }

        // ── Rail + Content ────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxSize()) {
            AutomotiveRail(
                currentRoute      = currentRoute,
                onNav             = ::navigate,
                fontScale         = railScale,
                preset1           = presetState.preset1,
                preset2           = presetState.preset2,
                onPresetClick     = { slot -> presetVm.launchPreset(slot) },
                onPresetLongClick = { slot -> pickerSlot = slot },
                modifier          = Modifier.onGloballyPositioned { coords ->
                    RailEdgePx = (coords.positionInWindow().x + coords.size.width).toInt()
                },
            )

            Box(modifier = Modifier.weight(1f).fillMaxHeight().clipToBounds()) {
                NavHost(
                    navController    = navController,
                    startDestination = NavRoute.Home.route
                ) {
                    composable(NavRoute.Home.route)       { HomeScreen() }
                    composable(NavRoute.Channels.route)   { StationsScreen() }
                    composable(NavRoute.Spotify.route)    { SpotifyScreen() }
                    composable(NavRoute.Categories.route) { CategoriesScreen() }
                    composable(NavRoute.Search.route)     { SearchScreen() }
                    composable(NavRoute.Recent.route)     { RecentScreen() }
                    composable(NavRoute.Settings.route) { SettingsScreen() }
                    composable(NavRoute.Apps.route)     { AppsScreen() }
                }
            }
        }

        if (pickerSlot > 0) {
            AppPickerDialog(
                slot      = pickerSlot,
                apps      = presetState.allApps,
                onSelect  = { app -> presetVm.setPreset(pickerSlot, app) },
                onDismiss = { pickerSlot = 0 },
            )
        }

        // ── Floating top-right overlay: weather + clock + settings ─────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .then(
                    Modifier
                ),
            horizontalAlignment = Alignment.End,
        ) {
            TopRightOverlay(
                currentRoute = currentRoute,
                onNav        = ::navigate,
                fontScale    = topBarScale
            )
            if (currentRoute == NavRoute.Home.route) {
                SpeedWidget()
            }
        }
    }
}
