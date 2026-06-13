package com.codelegger.golfperformancetracker.ui.players

import app.cash.turbine.test
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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
    fun refreshFailure_surfacesFriendlyError_butKeepsCachedPlayers() = runTest {
        val repo = FakePlayerRepository(
            initial = samplePlayers,
            // A network failure (UnknownHostException is an IOException).
            refreshResult = Result.failure(java.io.IOException("Unable to resolve host")),
        )
        val viewModel = PlayerListViewModel(repo) // init{} triggers refresh -> failure

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            // The raw exception text is mapped to a user-friendly message.
            assertEquals("No connection — showing saved data.", state.errorMessage)
            assertEquals(samplePlayers, state.players) // cache untouched
            assertFalse(state.isRefreshing)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refreshFailure_withEmptyCache_tellsUserToConnect() = runTest {
        val repo = FakePlayerRepository(
            initial = emptyList(), // first launch: nothing cached yet
            refreshResult = Result.failure(java.io.IOException("Unable to resolve host")),
        )
        val viewModel = PlayerListViewModel(repo)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            // We must not promise "saved data" when there is none to show.
            assertEquals("No connection — connect to load players.", state.errorMessage)
            assertEquals(emptyList<Player>(), state.players)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun query_filtersByName() = runTest {
        val viewModel = PlayerListViewModel(FakePlayerRepository(initial = samplePlayers))

        viewModel.onQueryChange("sam")

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(1, state.players.size)
            assertEquals("Sam Lee", state.players.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun query_filtersByClub_caseInsensitive() = runTest {
        val viewModel = PlayerListViewModel(FakePlayerRepository(initial = samplePlayers))

        viewModel.onQueryChange("DRIVER")

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(1, state.players.size)
            assertEquals("Jake Newman", state.players.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun blankQuery_returnsAllPlayers() = runTest {
        val viewModel = PlayerListViewModel(FakePlayerRepository(initial = samplePlayers))

        viewModel.onQueryChange("driver")
        viewModel.onQueryChange("")

        viewModel.uiState.test {
            assertEquals(2, expectMostRecentItem().players.size)
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

    override fun observePlayer(id: String): Flow<Player?> =
        players.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun refreshPlayers(): Result<Unit> = refreshResult
}
