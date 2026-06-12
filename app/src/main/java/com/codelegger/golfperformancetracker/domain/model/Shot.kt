package com.codelegger.golfperformancetracker.domain.model

/**
 * A single golf shot's performance metrics, belonging to a [Player].
 */
data class Shot(
    val id: String,
    val playerId: String,
    val ballSpeed: Double,      // mph
    val launchAngle: Double,    // degrees
    val carryDistance: Double,  // yards
    val clubType: String,       // e.g. "7i", "Driver"
    val spinRate: Int,          // rpm
    val createdAt: String?,     // ISO timestamp from the API, used for ordering
)
