package com.codelegger.golfperformancetracker.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One-to-many projection: a player together with all of their shots. Room assembles this
 * in a single query via [Relation], so the data layer never has to join by hand.
 */
data class PlayerWithShots(
    @Embedded val player: PlayerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "playerId",
    )
    val shots: List<ShotEntity>,
)
