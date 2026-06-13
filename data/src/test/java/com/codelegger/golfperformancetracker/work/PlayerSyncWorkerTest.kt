package com.codelegger.golfperformancetracker.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifies the background sync orchestration: the worker must refresh the roster **and** every
 * player's shots, and translate failures into [ListenableWorker.Result.retry] so WorkManager
 * re-runs with backoff.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PlayerSyncWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val players = listOf(
        Player("1", "Jake", "Driver", null, 150.0, 250.0),
        Player("2", "Sofia", "7i", null, 120.0, 170.0),
    )

    private fun buildWorker(playerRepo: PlayerRepository, shotRepo: ShotRepository): PlayerSyncWorker {
        val factory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters,
            ): ListenableWorker = PlayerSyncWorker(appContext, workerParameters, playerRepo, shotRepo)
        }
        return TestListenableWorkerBuilder<PlayerSyncWorker>(context)
            .setWorkerFactory(factory)
            .build()
    }

    @Test
    fun success_refreshesPlayersAndEveryPlayersShots() = runTest {
        val shotRepo = FakeShotRepository()
        val worker = buildWorker(FakePlayerRepository(players), shotRepo)

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        // Crucially, shots are refreshed for *every* player, not just the roster.
        assertEquals(setOf("1", "2"), shotRepo.refreshedFor.toSet())
    }

    @Test
    fun playerRefreshFailure_retries_andSkipsShots() = runTest {
        val shotRepo = FakeShotRepository()
        val worker = buildWorker(
            FakePlayerRepository(players, refreshResult = Result.failure(RuntimeException("offline"))),
            shotRepo,
        )

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
        assertEquals(0, shotRepo.refreshedFor.size)
    }

    @Test
    fun shotRefreshFailure_retries() = runTest {
        val shotRepo = FakeShotRepository(failFor = setOf("2"))
        val worker = buildWorker(FakePlayerRepository(players), shotRepo)

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }
}

private class FakePlayerRepository(
    initial: List<Player>,
    private val refreshResult: Result<Unit> = Result.success(Unit),
) : PlayerRepository {
    private val players = MutableStateFlow(initial)
    override fun observePlayers(): Flow<List<Player>> = players
    override fun observePlayer(id: String): Flow<Player?> =
        players.map { list -> list.firstOrNull { it.id == id } }
    override suspend fun refreshPlayers(): Result<Unit> = refreshResult
}

private class FakeShotRepository(
    private val failFor: Set<String> = emptySet(),
) : ShotRepository {
    val refreshedFor = mutableListOf<String>()
    override fun observeShots(playerId: String): Flow<List<Shot>> = MutableStateFlow(emptyList())
    override suspend fun refreshShots(playerId: String): Result<Unit> {
        refreshedFor.add(playerId)
        return if (playerId in failFor) Result.failure(RuntimeException("offline")) else Result.success(Unit)
    }
}
