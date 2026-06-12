package com.codelegger.golfperformancetracker.ui.players

import app.cash.turbine.test
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PlayerListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val samplePlayers = listOf(
        Player("1", "Jake Newman", "Driver", null, 156.0, 268.0),
        Player("2", "Sam Lee", "7 Iron", null, 118.0, 172.0),
    )

    @Test
    fun emitsPlayersFromRepositoryCache() = runTest {
        val repo = FakePlayerRepository(initial = samplePlayers)
        val viewModel = PlayerListViewModel(repo)

        viewModel.uiState.test {
            // Skip the initial loading state, assert the cache-backed state.
            val state = expectMostRecentItem()
            assertEquals(samplePlayers, state.players)
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshFailure_surfacesError_butKeepsCachedPlayers() = runTest {
        val repo = FakePlayerRepository(
            initial = samplePlayers,
            refreshResult = Result.failure(RuntimeException("offline")),
        )
        val viewModel = PlayerListViewModel(repo) // init{} triggers refresh -> failure

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("offline", state.errorMessage)
            assertEquals(samplePlayers, state.players) // cache untouched
            assertFalse(state.isRefreshing)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onErrorShown_clearsError() = runTest {
        val repo = FakePlayerRepository(
            initial = samplePlayers,
            refreshResult = Result.failure(RuntimeException("offline")),
        )
        val viewModel = PlayerListViewModel(repo)

        viewModel.onErrorShown()

        viewModel.uiState.test {
            assertNull(expectMostRecentItem().errorMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

/** Hand-written fake — simpler and clearer than mocking a Flow for these tests. */
private class FakePlayerRepository(
    initial: List<Player>,
    private val refreshResult: Result<Unit> = Result.success(Unit),
) : PlayerRepository {

    private val players = MutableStateFlow(initial)

    override fun observePlayers(): Flow<List<Player>> = players

    override suspend fun refreshPlayers(): Result<Unit> = refreshResult
}
