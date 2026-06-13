package com.codelegger.golfperformancetracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

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

    override suspend fun doWork(): Result {
        Timber.i("PlayerSyncWorker: starting background player sync")
        return playerRepository.refreshPlayers().fold(
            onSuccess = {
                Timber.i("PlayerSyncWorker: sync succeeded")
                Result.success()
            },
            onFailure = {
                Timber.w(it, "PlayerSyncWorker: sync failed, will retry")
                Result.retry()
            },
        )
    }

    companion object {
        const val UNIQUE_WORK_NAME = "player-sync"
    }
}
