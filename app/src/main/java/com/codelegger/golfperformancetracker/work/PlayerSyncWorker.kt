package com.codelegger.golfperformancetracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background worker that refreshes the player cache.
 *
 * `@HiltWorker` + `@AssistedInject` let Hilt provide the [PlayerRepository] (via the
 * HiltWorkerFactory wired up in GolfApplication). On network failure it returns
 * [Result.retry] so WorkManager re-runs it with backoff — the cache stays as-is meanwhile.
 */
@HiltWorker
class PlayerSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val playerRepository: PlayerRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result =
        playerRepository.refreshPlayers().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )

    companion object {
        const val UNIQUE_WORK_NAME = "player-sync"
    }
}
