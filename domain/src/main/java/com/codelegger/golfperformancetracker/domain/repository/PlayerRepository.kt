package com.codelegger.golfperformancetracker.domain.repository

import com.codelegger.golfperformancetracker.domain.model.Player
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for players.
 *
 * [observePlayers] always streams from the local cache (Room), so the UI works offline.
 * [refreshPlayers] fetches from the network and writes into that cache; observers update
 * automatically. Network success/failure is returned as a [Result] so the caller can decide
 * how to surface it — without ever clobbering already-cached data.
 */
interface PlayerRepository {

    fun observePlayers(): Flow<List<Player>>

    /** Streams a single player from the cache by id, or `null` if not cached. */
    fun observePlayer(id: String): Flow<Player?>

    suspend fun refreshPlayers(): Result<Unit>
}
