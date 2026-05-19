package com.bgautoradio.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.R
import com.bgautoradio.ui.FontScaleViewModel
import com.bgautoradio.ui.components.AutomotiveRail
import com.bgautoradio.ui.components.AutomotiveTopBar
import com.bgautoradio.ui.screens.categories.CategoriesScreen
import com.bgautoradio.ui.screens.favorites.FavoritesScreen
import com.bgautoradio.ui.screens.home.HomeScreen
import com.bgautoradio.ui.screens.navigation.NavigationScreen
import com.bgautoradio.ui.screens.recent.RecentScreen
import com.bgautoradio.ui.screens.search.SearchScreen
import com.bgautoradio.ui.screens.settings.SettingsScreen
import com.bgautoradio.ui.screens.stations.StationsScreen
sealed class NavRoute(val route: String) {
    object Home       : NavRoute("home")
    object Channels   : NavRoute("channels")
    object Favorites  : NavRoute("favorites")
    object Categories : NavRoute("categories")
    object Search     : NavRoute("search")
    object Recent     : NavRoute("recent")
    object Settings   : NavRoute("settings")
    object Navigation : NavRoute("navigation")
}

@Composable
fun AppNavigation() {
    val navController  = rememberNavController()
    val backStack      by navController.currentBackStackEntryAsState()
    val currentRoute   = backStack?.destination?.route ?: NavRoute.Home.route

    val fontVm: FontScaleViewModel = hiltViewModel()
    val topBarScale by fontVm.topBarScale.collectAsStateWithLifecycle()
    val railScale   by fontVm.railScale.collectAsStateWithLifecycle()

    fun navigate(route: String) {
        if (route == currentRoute) return
        navController.navigate(route) {
            popUpTo(NavRoute.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState    = true
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Background image — lowest layer, covers entire screen
        Image(
            painter      = painterResource(R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier     = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // ── TopBar ────────────────────────────────────────────────────────
            AutomotiveTopBar(currentRoute = currentRoute, onNav = ::navigate, fontScale = topBarScale)

            // ── Rail + Content ────────────────────────────────────────────────
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AutomotiveRail(currentRoute = currentRoute, onNav = ::navigate, fontScale = railScale)

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    NavHost(
                        navController    = navController,
                        startDestination = NavRoute.Home.route
                    ) {
                        composable(NavRoute.Home.route)       { HomeScreen() }
                        composable(NavRoute.Channels.route)   { StationsScreen() }
                        composable(NavRoute.Favorites.route)  { FavoritesScreen() }
                        composable(NavRoute.Categories.route) { CategoriesScreen() }
                        composable(NavRoute.Search.route)     { SearchScreen() }
                        composable(NavRoute.Recent.route)     { RecentScreen() }
                        composable(NavRoute.Settings.route)   { SettingsScreen() }
                        composable(NavRoute.Navigation.route) { NavigationScreen() }
                    }
                }
            }
        }
    }
}
