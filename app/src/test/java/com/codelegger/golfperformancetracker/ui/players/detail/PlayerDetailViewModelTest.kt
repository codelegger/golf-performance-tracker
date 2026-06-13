package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import androidx.paging.PagingData
import com.codelegger.golfperformancetracker.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PlayerDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val players = listOf(
        Player("1", "Jake Newman", "Driver", null, 167.0, 291.0),
        Player("2", "Sofia Martinez", "7 Iron", null, 118.0, 172.0),
    )

    private val shots = listOf(
        Shot("s1", "1", 168.0, 11.4, 293.0, "Driver", 2480, null),
        Shot("s2", "1", 140.3, 17.2, 212.0, "4i", 4900, null),
    )

    @Test
    fun emitsPlayerAndShotsForNavArgId() = runTest {
        val viewModel = PlayerDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("playerId" to "1")),
            playerRepository = FakePlayerRepository(players),
            shotRepository = FakeShotRepository(shots),
        )

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("Jake Newman", state.player?.name)
            assertEquals(2, state.shots.size)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshShotsIsRequestedOnInit() = runTest {
        val shotRepo = FakeShotRepository(shots)

        PlayerDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("playerId" to "1")),
            playerRepository = FakePlayerRepository(players),
            shotRepository = shotRepo,
        )

        assertTrue(shotRepo.refreshedFor.contains("1"))
    }

    @Test
    fun unknownId_yieldsNullPlayerAndNoShots() = runTest {
        val viewModel = PlayerDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("playerId" to "999")),
            playerRepository = FakePlayerRepository(players),
            shotRepository = FakeShotRepository(shots),
        )

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertNull(state.player)
            assertTrue(state.shots.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakePlayerRepository(initial: List<Player>) : PlayerRepository {
    private val players = MutableStateFlow(initial)
    override fun observePlayers(): Flow<List<Player>> = players
    override fun observePlayer(id: String): Flow<Player?> =
        players.map { list -> list.firstOrNull { it.id == id } }
    override suspend fun refreshPlayers(): Result<Unit> = Result.success(Unit)
    override fun pagedPlayers(): Flow<PagingData<Player>> = flowOf(PagingData.empty())
}

private class FakeShotRepository(initial: List<Shot>) : ShotRepository {
    private val shots = MutableStateFlow(initial)
    val refreshedFor = mutableListOf<String>()
    override fun observeShots(playerId: String): Flow<List<Shot>> =
        shots.map { list -> list.filter { it.playerId == playerId } }
    override suspend fun refreshShots(playerId: String): Result<Unit> {
        refreshedFor.add(playerId)
        return Result.success(Unit)
    }
}
