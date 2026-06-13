package com.codelegger.golfperformancetracker.data.repository

import com.codelegger.golfperformancetracker.data.local.GolfDao
import com.codelegger.golfperformancetracker.data.local.entity.PlayerEntity
import com.codelegger.golfperformancetracker.data.local.entity.PlayerWithShots
import com.codelegger.golfperformancetracker.data.local.entity.ShotEntity
import com.codelegger.golfperformancetracker.data.remote.GolfApi
import com.codelegger.golfperformancetracker.data.remote.dto.PlayerDto
import com.codelegger.golfperformancetracker.data.remote.dto.ShotDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * In-memory [GolfDao] for repository tests. It mirrors the relevant Room semantics — including
 * the foreign-key CASCADE from players to shots — so the reconciliation logic in the DAO's
 * default `replacePlayers`/`replaceShots` methods is exercised end-to-end without a real DB.
 * (The actual SQL is verified separately by the Robolectric `GolfDaoTest`.)
 */
internal class FakeGolfDao : GolfDao {
    private val players = MutableStateFlow<List<PlayerEntity>>(emptyList())
    private val shots = MutableStateFlow<List<ShotEntity>>(emptyList())

    override fun observePlayers(): Flow<List<PlayerEntity>> =
        players.map { list -> list.sortedBy(PlayerEntity::name) }

    override fun observePlayer(id: String): Flow<PlayerEntity?> =
        players.map { list -> list.firstOrNull { it.id == id } }

    override fun observePlayerWithShots(id: String): Flow<PlayerWithShots?> =
        combine(players, shots) { p, s ->
            val player = p.firstOrNull { it.id == id } ?: return@combine null
            PlayerWithShots(player, s.filter { it.playerId == id })
        }

    override fun observeShotsForPlayer(playerId: String): Flow<List<ShotEntity>> =
        shots.map { list -> list.filter { it.playerId == playerId } }

    override suspend fun upsertPlayers(players: List<PlayerEntity>) {
        val byId = this.players.value.associateBy { it.id }.toMutableMap()
        players.forEach { byId[it.id] = it }
        this.players.value = byId.values.toList()
    }

    override suspend fun upsertShots(shots: List<ShotEntity>) {
        val byId = this.shots.value.associateBy { it.id }.toMutableMap()
        shots.forEach { byId[it.id] = it }
        this.shots.value = byId.values.toList()
    }

    override suspend fun clearPlayers() {
        players.value = emptyList()
        shots.value = emptyList() // FK CASCADE
    }

    override suspend fun deletePlayersNotIn(keepIds: List<String>) {
        players.value = players.value.filter { it.id in keepIds }
        shots.value = shots.value.filter { it.playerId in keepIds } // FK CASCADE
    }

    override suspend fun deleteShotsForPlayer(playerId: String) {
        shots.value = shots.value.filterNot { it.playerId == playerId }
    }

    override suspend fun deleteShotsNotIn(playerId: String, keepIds: List<String>) {
        shots.value = shots.value.filterNot { it.playerId == playerId && it.id !in keepIds }
    }
}

/** Configurable [GolfApi] double — set the payloads, or flip the flags to simulate offline. */
internal class FakeGolfApi : GolfApi {
    var players: List<PlayerDto> = emptyList()
    var shotsByPlayer: Map<String, List<ShotDto>> = emptyMap()
    var failPlayers = false
    var failShots = false

    override suspend fun getPlayers(): List<PlayerDto> {
        if (failPlayers) throw IOException("simulated offline")
        return players
    }

    override suspend fun getShots(playerId: String): List<ShotDto> {
        if (failShots) throw IOException("simulated offline")
        return shotsByPlayer[playerId].orEmpty()
    }
}
