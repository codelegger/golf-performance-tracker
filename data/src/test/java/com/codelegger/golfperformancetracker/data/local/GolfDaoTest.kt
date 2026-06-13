package com.codelegger.golfperformancetracker.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.codelegger.golfperformancetracker.data.local.entity.PlayerEntity
import com.codelegger.golfperformancetracker.data.local.entity.ShotEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Real Room integration test (in-memory SQLite via Robolectric). Exercises the hardest seam in
 * the cache: the transactional reconciliation in [GolfDao.replacePlayers] / [GolfDao.replaceShots]
 * and the foreign-key CASCADE from players to their shots.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class GolfDaoTest {

    private lateinit var db: GolfDatabase
    private lateinit var dao: GolfDao

    private fun player(id: String, name: String = "P$id") =
        PlayerEntity(id, name, "Driver", null, 150.0, 250.0)

    private fun shot(id: String, playerId: String) =
        ShotEntity(id, playerId, 150.0, 12.0, 240.0, "7i", 6000, null)

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GolfDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.golfDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun replacePlayers_upsertsFreshSet_andPrunesRemovedPlayers() = runTest {
        dao.replacePlayers(listOf(player("1"), player("2")))
        dao.replacePlayers(listOf(player("1", name = "Renamed")))

        val players = dao.observePlayers().first()
        assertEquals(1, players.size)
        assertEquals("1", players.single().id)
        assertEquals("Renamed", players.single().name) // upsert updated the kept row
    }

    @Test
    fun replacePlayers_cascadeDeletesShotsOfRemovedPlayers() = runTest {
        dao.replacePlayers(listOf(player("1"), player("2")))
        dao.replaceShots("1", listOf(shot("s1", "1")))
        dao.replaceShots("2", listOf(shot("s2", "2")))

        // Player 2 is gone after this refresh; its shots must CASCADE away.
        dao.replacePlayers(listOf(player("1")))

        assertEquals(listOf("s1"), dao.observeShotsForPlayer("1").first().map { it.id })
        assertTrue(dao.observeShotsForPlayer("2").first().isEmpty())
    }

    @Test
    fun replaceShots_prunesShotsRemovedForThatPlayer_leavingOthers() = runTest {
        dao.replacePlayers(listOf(player("1")))
        dao.replaceShots("1", listOf(shot("s1", "1"), shot("s2", "1")))

        dao.replaceShots("1", listOf(shot("s1", "1")))

        assertEquals(listOf("s1"), dao.observeShotsForPlayer("1").first().map { it.id })
    }

    @Test
    fun replacePlayers_emptyList_clearsEverything() = runTest {
        dao.replacePlayers(listOf(player("1")))
        dao.replaceShots("1", listOf(shot("s1", "1")))

        dao.replacePlayers(emptyList())

        assertTrue(dao.observePlayers().first().isEmpty())
        assertTrue(dao.observeShotsForPlayer("1").first().isEmpty())
    }
}
