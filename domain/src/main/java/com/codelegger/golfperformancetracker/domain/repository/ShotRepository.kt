package com.codelegger.golfperformancetracker.domain.repository

import com.codelegger.golfperformancetracker.domain.model.Shot
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for a player's shots. Same offline-first contract as
 * [PlayerRepository]: [observeShots] streams from Room; [refreshShots] fetches and caches.
 *
 * Kept separate from [PlayerRepository] (one repository per concern) so each stays focused
 * and independently testable.
 */
interface ShotRepository {

    fun observeShots(playerId: String): Flow<List<Shot>>

    suspend fun refreshShots(playerId: String): Result<Unit>
}
