package com.codelegger.golfperformancetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codelegger.golfperformancetracker.ui.players.PlayerListScreen

/**
 * Root navigation graph. Starts on the player list; feature destinations (player detail,
 * shots) are wired in here as they are built.
 */
@Composable
fun GolfNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = GolfDestinations.PLAYERS,
    ) {
        composable(GolfDestinations.PLAYERS) {
            PlayerListScreen(
                // TODO(slice 2): navigate to player detail
                onPlayerClick = { /* playerId -> navController.navigate(...) */ },
            )
        }
    }
}
