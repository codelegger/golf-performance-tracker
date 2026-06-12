package com.codelegger.golfperformancetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codelegger.golfperformancetracker.ui.home.HomeScreen

/**
 * Root navigation graph. Hosts the single [GolfDestinations.HOME] destination for now;
 * feature destinations are wired in here as they are built.
 */
@Composable
fun GolfNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = GolfDestinations.HOME,
    ) {
        composable(GolfDestinations.HOME) {
            HomeScreen()
        }
    }
}
