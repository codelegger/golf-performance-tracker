package com.codelegger.golfperformancetracker.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.codelegger.golfperformancetracker.data.local.entity.PlayerEntity
import com.codelegger.golfperformancetracker.data.local.entity.PlayerWithShots
import com.codelegger.golfperformancetracker.data.local.entity.ShotEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data-access object for the golf cache.
 *
 * Reads return [Flow]s so the UI observes the database reactively — this is what makes Room
 * the Single Source of Truth: the network writes here, and the UI re-renders automatically.
 * `@Upsert` inserts-or-updates so a refresh from the API is idempotent.
 */
@Dao
interface GolfDao {

    @Query("SELECT * FROM players ORDER BY name ASC")
    fun observePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    fun observePlayer(id: String): Flow<PlayerEntity?>

    @Transaction
    @Query("SELECT * FROM players WHERE id = :id")
    fun observePlayerWithShots(id: String): Flow<PlayerWithShots?>

    @Query("SELECT * FROM shots WHERE playerId = :playerId ORDER BY createdAt DESC")
    fun observeShotsForPlayer(playerId: String): Flow<List<ShotEntity>>

    @Upsert
    suspend fun upsertPlayers(players: List<PlayerEntity>)

    @Upsert
    suspend fun upsertShots(shots: List<ShotEntity>)

    @Query("DELETE FROM players")
    suspend fun clearPlayers()
}
