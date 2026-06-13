package com.codelegger.golfperformancetracker.data.repository

import com.codelegger.golfperformancetracker.data.remote.dto.PlayerDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerRepositoryImplTest {

    private val api = FakeGolfApi()
    private val dao = FakeGolfDao()
    private val repo = PlayerRepositoryImpl(api, dao, UnconfinedTestDispatcher())

    private fun player(id: String, name: String) =
        PlayerDto(id = id, name = name, club = "Driver", averageBallSpeed = 150.0, averageCarryDistance = 250.0)

    @Test
    fun refreshPlayers_cachesNetworkPlayers_andExposesAsDomain() = runTest {
        api.players = listOf(player("1", "Jake"), player("2", "Sofia"))

        val result = repo.refreshPlayers()

        assertTrue(result.isSuccess)
        val cached = repo.observePlayers().first()
        assertEquals(2, cached.size)
        assertEquals("Jake", cached.first { it.id == "1" }.name)
    }

    @Test
    fun refreshPlayers_prunesPlayersRemovedFromBackend() = runTest {
        api.players = listOf(player("1", "Jake"), player("2", "Sofia"))
        repo.refreshPlayers()

        // Backend no longer returns player 2.
        api.players = listOf(player("1", "Jake"))
        repo.refreshPlayers()

        val players = repo.observePlayers().first()
        assertEquals(1, players.size)
        assertEquals("1", players.single().id)
    }

    @Test
    fun refreshPlayers_failure_returnsFailure_andLeavesCacheUntouched() = runTest {
        api.players = listOf(player("1", "Jake"))
        repo.refreshPlayers()

        api.failPlayers = true
        val result = repo.refreshPlayers()

        assertTrue(result.isFailure)
        assertEquals(1, repo.observePlayers().first().size) // cache preserved
    }

    @Test
    fun refreshPlayers_emptyBackend_clearsCache() = runTest {
        api.players = listOf(player("1", "Jake"))
        repo.refreshPlayers()

        api.players = emptyList()
        repo.refreshPlayers()

        assertTrue(repo.observePlayers().first().isEmpty())
        assertFalse(repo.observePlayer("1").first() != null)
    }
}
