package com.codelegger.golfperformancetracker.data.mapper

import com.codelegger.golfperformancetracker.data.remote.dto.PlayerDto
import com.codelegger.golfperformancetracker.data.remote.dto.ShotDto
import org.junit.Assert.assertEquals
import org.junit.Test

/** Verifies the DTO → Entity → domain mapping chain preserves data faithfully. */
class GolfMappersTest {

    @Test
    fun playerDto_mapsThroughEntity_toDomain() {
        val dto = PlayerDto(
            id = "1",
            name = "Jake Newman",
            club = "Driver",
            avatarUrl = "https://img.example/jake.png",
            averageBallSpeed = 156.3,
            averageCarryDistance = 268.0,
        )

        val domain = dto.toEntity().toDomain()

        assertEquals("1", domain.id)
        assertEquals("Jake Newman", domain.name)
        assertEquals("Driver", domain.club)
        assertEquals("https://img.example/jake.png", domain.avatarUrl)
        assertEquals(156.3, domain.averageBallSpeed, 0.001)
        assertEquals(268.0, domain.averageCarryDistance, 0.001)
    }

    @Test
    fun shotDto_withoutPlayerId_usesFallback() {
        val dto = ShotDto(
            id = "s1",
            ballSpeed = 150.0,
            launchAngle = 12.5,
            carryDistance = 240.0,
            clubType = "7i",
            spinRate = 6500,
            createdAt = "2026-06-12T10:00:00Z",
        )

        val entity = dto.toEntity(fallbackPlayerId = "player-1")

        assertEquals("player-1", entity.playerId)

        val domain = entity.toDomain()
        assertEquals("s1", domain.id)
        assertEquals(150.0, domain.ballSpeed, 0.001)
        assertEquals(12.5, domain.launchAngle, 0.001)
        assertEquals(6500, domain.spinRate)
    }

    @Test
    fun shotDto_withPlayerId_keepsOwnId() {
        val dto = ShotDto(id = "s2", playerId = "player-9", clubType = "Driver")

        val entity = dto.toEntity(fallbackPlayerId = "player-1")

        assertEquals("player-9", entity.playerId)
    }
}
