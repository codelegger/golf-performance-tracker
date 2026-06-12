package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.lifecycle.SavedStateHandle
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

class PlayerDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val players = listOf(
        Player("1", "Jake Newman", "Driver", null, 167.0, 291.0),
        Player("2", "Sofia Martinez", "7 Iron", null, 118.0, 172.0),
    )

    @Test
    fun emitsPlayerMatchingNavArgId() = runTest {
        val viewModel = PlayerDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("playerId" to "2")),
            repository = FakePlayerRepository(players),
        )

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("Sofia Martinez", state.player?.name)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun unknownId_yieldsNullPlayer() = runTest {
        val viewModel = PlayerDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("playerId" to "999")),
            repository = FakePlayerRepository(players),
        )

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertNull(state.player)
            assertFalse(state.isLoading)
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
}
