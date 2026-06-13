package com.codelegger.golfperformancetracker.data.repository

import com.codelegger.golfperformancetracker.data.local.GolfDao
import com.codelegger.golfperformancetracker.data.local.entity.ShotEntity
import com.codelegger.golfperformancetracker.data.mapper.toDomain
import com.codelegger.golfperformancetracker.data.mapper.toEntity
import com.codelegger.golfperformancetracker.data.remote.GolfApi
import com.codelegger.golfperformancetracker.di.IoDispatcher
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Offline-first [ShotRepository]: reads from Room, refreshes from `/players/{id}/shots`.
 * The DTO mapper supplies [fallbackPlayerId] since the nested endpoint omits it on each shot.
 */
class ShotRepositoryImpl @Inject constructor(
    private val api: GolfApi,
    private val dao: GolfDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ShotRepository {

    override fun observeShots(playerId: String): Flow<List<Shot>> =
        dao.observeShotsForPlayer(playerId).map { entities -> entities.map(ShotEntity::toDomain) }

    override suspend fun refreshShots(playerId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val remote = api.getShots(playerId)
            dao.upsertShots(remote.map { it.toEntity(fallbackPlayerId = playerId) })
            Timber.d("Refreshed %d shots for player %s", remote.size, playerId)
        }.onFailure { Timber.w(it, "Shot refresh failed for player %s; serving cache", playerId) }
    }
}
