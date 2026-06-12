package com.codelegger.golfperformancetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Room table for players — the local cache that backs offline access. */
@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val club: String,
    val avatarUrl: String?,
    val averageBallSpeed: Double,
    val averageCarryDistance: Double,
)
