package com.codelegger.golfperformancetracker.data.mapper

import com.codelegger.golfperformancetracker.data.local.entity.PlayerEntity
import com.codelegger.golfperformancetracker.data.local.entity.ShotEntity
import com.codelegger.golfperformancetracker.data.remote.dto.PlayerDto
import com.codelegger.golfperformancetracker.data.remote.dto.ShotDto
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.model.Shot

/*
 * Mapping functions between the three representations of our data:
 *   DTO (network)  ──►  Entity (database)  ──►  domain model (app)
 *
 * Keeping these as small, pure extension functions makes each hop trivially unit-testable
 * and keeps the boundaries between layers explicit.
 */

// --- Player ---

fun PlayerDto.toEntity(): PlayerEntity = PlayerEntity(
    id = id,
    name = name,
    club = club,
    avatarUrl = avatarUrl,
    averageBallSpeed = averageBallSpeed,
    averageCarryDistance = averageCarryDistance,
)

fun PlayerEntity.toDomain(): Player = Player(
    id = id,
    name = name,
    club = club,
    avatarUrl = avatarUrl,
    averageBallSpeed = averageBallSpeed,
    averageCarryDistance = averageCarryDistance,
)

// --- Shot ---

/**
 * [fallbackPlayerId] is used when the DTO omits its own [ShotDto.playerId] (e.g. when shots
 * come from a nested /players/{id}/shots endpoint).
 */
fun ShotDto.toEntity(fallbackPlayerId: String): ShotEntity = ShotEntity(
    id = id,
    playerId = playerId ?: fallbackPlayerId,
    ballSpeed = ballSpeed,
    launchAngle = launchAngle,
    carryDistance = carryDistance,
    clubType = clubType,
    spinRate = spinRate,
    createdAt = createdAt,
)

fun ShotEntity.toDomain(): Shot = Shot(
    id = id,
    playerId = playerId,
    ballSpeed = ballSpeed,
    launchAngle = launchAngle,
    carryDistance = carryDistance,
    clubType = clubType,
    spinRate = spinRate,
    createdAt = createdAt,
)
