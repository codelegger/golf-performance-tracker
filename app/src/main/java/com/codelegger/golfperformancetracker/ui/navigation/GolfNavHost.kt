package com.codelegger.golfperformancetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codelegger.golfperformancetracker.ui.players.PlayerListScreen
import com.codelegger.golfperformancetracker.ui.players.detail.PlayerDetailScreen
import com.codelegger.golfperformancetracker.ui.players.detail.PlayerDetailViewModel

/**
 * Root navigation graph. Starts on the player list; tapping a player opens its detail.
 * The shots destination is wired in here as it is built.
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
                onPlayerClick = { playerId ->
                    navController.navigate(GolfDestinations.playerDetail(playerId))
                },
            )
        }

        composable(
            route = GolfDestinations.PLAYER_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(PlayerDetailViewModel.PLAYER_ID_ARG) { type = NavType.StringType },
            ),
        ) {
            PlayerDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
