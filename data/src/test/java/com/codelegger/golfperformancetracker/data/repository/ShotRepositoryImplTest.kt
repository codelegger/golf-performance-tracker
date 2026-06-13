package com.codelegger.golfperformancetracker.data.repository

import com.codelegger.golfperformancetracker.data.remote.dto.ShotDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShotRepositoryImplTest {

    private val api = FakeGolfApi()
    private val dao = FakeGolfDao()
    private val repo = ShotRepositoryImpl(api, dao, UnconfinedTestDispatcher())

    private fun shot(id: String, playerId: String? = null) =
        ShotDto(id = id, playerId = playerId, ballSpeed = 150.0, carryDistance = 240.0, clubType = "7i", spinRate = 6000)

    @Test
    fun refreshShots_cachesShots_applyingFallbackPlayerIdFromNestedEndpoint() = runTest {
        // Nested /players/{id}/shots omits playerId; the repo must fall back to the path id.
        api.shotsByPlayer = mapOf("p1" to listOf(shot("s1"), shot("s2")))

        val result = repo.refreshShots("p1")

        assertTrue(result.isSuccess)
        val cached = repo.observeShots("p1").first()
        assertEquals(2, cached.size)
        assertTrue(cached.all { it.playerId == "p1" })
    }

    @Test
    fun refreshShots_prunesShotsRemovedFromBackend_forThatPlayerOnly() = runTest {
        api.shotsByPlayer = mapOf(
            "p1" to listOf(shot("s1"), shot("s2")),
            "p2" to listOf(shot("s3")),
        )
        repo.refreshShots("p1")
        repo.refreshShots("p2")

        // p1 loses s2; p2 is untouched.
        api.shotsByPlayer = mapOf("p1" to listOf(shot("s1")), "p2" to listOf(shot("s3")))
        repo.refreshShots("p1")

        assertEquals(listOf("s1"), repo.observeShots("p1").first().map { it.id })
        assertEquals(listOf("s3"), repo.observeShots("p2").first().map { it.id })
    }

    @Test
    fun refreshShots_failure_returnsFailure_andKeepsCache() = runTest {
        api.shotsByPlayer = mapOf("p1" to listOf(shot("s1")))
        repo.refreshShots("p1")

        api.failShots = true
        val result = repo.refreshShots("p1")

        assertTrue(result.isFailure)
        assertEquals(1, repo.observeShots("p1").first().size)
    }

    @Test
    fun refreshShots_emptyBackend_clearsThatPlayersShots() = runTest {
        api.shotsByPlayer = mapOf("p1" to listOf(shot("s1"), shot("s2")))
        repo.refreshShots("p1")

        api.shotsByPlayer = mapOf("p1" to emptyList())
        repo.refreshShots("p1")

        assertTrue(repo.observeShots("p1").first().isEmpty())
    }
}
