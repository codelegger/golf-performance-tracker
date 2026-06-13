package com.codelegger.golfperformancetracker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Background worker that refreshes the offline cache.
 *
 * `@HiltWorker` + `@AssistedInject` let Hilt provide the repositories (via the
 * HiltWorkerFactory wired up in GolfApplication). It refreshes the player roster **and**
 * each player's shots, so the performance data — the core product value — stays warm offline
 * instead of only updating when the user opens a player detail screen. On any failure it
 * returns [Result.retry] so WorkManager re-runs it with backoff; the cache stays as-is meanwhile.
 */
@HiltWorker
class PlayerSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val playerRepository: PlayerRepository,
    private val shotRepository: ShotRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.i("PlayerSyncWorker: starting background sync")

        // 1) Refresh the player roster first. If this fails (e.g. offline), retry later;
        //    there's no point fanning out to shots without an up-to-date player list.
        val playersResult = playerRepository.refreshPlayers()
        if (playersResult.isFailure) {
            Timber.w(playersResult.exceptionOrNull(), "PlayerSyncWorker: player sync failed, will retry")
            return Result.retry()
        }

        // 2) Refresh every player's shots so performance data is current offline.
        val players = playerRepository.observePlayers().first()
        val shotFailures = players.count { player ->
            shotRepository.refreshShots(player.id).isFailure
        }

        return if (shotFailures > 0) {
            Timber.w("PlayerSyncWorker: %d/%d shot refreshes failed, will retry", shotFailures, players.size)
            Result.retry()
        } else {
            Timber.i("PlayerSyncWorker: sync succeeded (%d players)", players.size)
            Result.success()
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "player-sync"
    }
}
