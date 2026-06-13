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
 *
 * Refreshes go through [replacePlayers] / [replaceShots], which upsert the fresh set **and**
 * delete locally-cached rows the backend no longer returns. Without this, a player or shot
 * deleted server-side would linger in the cache forever — a classic offline-first orphan.
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

    @Query("DELETE FROM players WHERE id NOT IN (:keepIds)")
    suspend fun deletePlayersNotIn(keepIds: List<String>)

    @Query("DELETE FROM shots WHERE playerId = :playerId")
    suspend fun deleteShotsForPlayer(playerId: String)

    @Query("DELETE FROM shots WHERE playerId = :playerId AND id NOT IN (:keepIds)")
    suspend fun deleteShotsNotIn(playerId: String, keepIds: List<String>)

    /**
     * Reconcile the players table against the full set returned by the backend: upsert the
     * fresh rows, then drop any cached player the backend no longer lists (CASCADE removes
     * their shots). Atomic via [Transaction] so observers never see a half-applied state.
     */
    @Transaction
    suspend fun replacePlayers(players: List<PlayerEntity>) {
        upsertPlayers(players)
        // `NOT IN ()` is invalid SQL, so clear everything when the backend returns nothing.
        if (players.isEmpty()) clearPlayers() else deletePlayersNotIn(players.map { it.id })
    }

    /**
     * Reconcile one player's shots: upsert the fresh set, then drop cached shots the backend
     * no longer returns for that player.
     */
    @Transaction
    suspend fun replaceShots(playerId: String, shots: List<ShotEntity>) {
        upsertShots(shots)
        if (shots.isEmpty()) deleteShotsForPlayer(playerId)
        else deleteShotsNotIn(playerId, shots.map { it.id })
    }
}
