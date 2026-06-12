package com.codelegger.golfperformancetracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room table for shots. A foreign key to [PlayerEntity] with CASCADE delete keeps the
 * cache consistent (deleting a player removes their shots); the index on [playerId]
 * keeps per-player lookups fast.
 */
@Entity(
    tableName = "shots",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("playerId")],
)
data class ShotEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val clubType: String,
    val spinRate: Int,
    val createdAt: String?,
)
