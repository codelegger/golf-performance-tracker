package com.codelegger.golfperformancetracker.ui.navigation

/**
 * Route catalog for the app's navigation graph.
 *
 * The shots destination is added with its slice.
 */
object GolfDestinations {
    const val PLAYERS = "players"

    const val PLAYER_DETAIL_ROUTE = "players/{playerId}"

    /** Builds a concrete detail route for [playerId]. */
    fun playerDetail(playerId: String): String = "players/$playerId"
}
