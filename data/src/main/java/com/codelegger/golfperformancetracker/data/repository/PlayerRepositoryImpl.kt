package com.codelegger.golfperformancetracker.data.repository

import com.codelegger.golfperformancetracker.data.local.GolfDao
import com.codelegger.golfperformancetracker.data.local.entity.PlayerEntity
import com.codelegger.golfperformancetracker.data.mapper.toDomain
import com.codelegger.golfperformancetracker.data.mapper.toEntity
import com.codelegger.golfperformancetracker.data.remote.GolfApi
import com.codelegger.golfperformancetracker.di.IoDispatcher
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Offline-first [PlayerRepository]: reads come from Room, writes come from the network.
 *
 * The Room read is mapped DB-entity → domain so the rest of the app never sees persistence
 * types. [refreshPlayers] runs on the IO dispatcher and wraps the network+DB work in
 * [runCatching] so a failure (e.g. no connectivity) becomes a [Result] failure rather than a
 * crash — and crucially, leaves the cached data untouched.
 */
class PlayerRepositoryImpl @Inject constructor(
    private val api: GolfApi,
    private val dao: GolfDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PlayerRepository {

    override fun observePlayers(): Flow<List<Player>> =
        dao.observePlayers().map { entities -> entities.map(PlayerEntity::toDomain) }

    override fun observePlayer(id: String): Flow<Player?> =
        dao.observePlayer(id).map { it?.toDomain() }

    override suspend fun refreshPlayers(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val remote = api.getPlayers()
            dao.upsertPlayers(remote.map { it.toEntity() })
        }
    }
}
