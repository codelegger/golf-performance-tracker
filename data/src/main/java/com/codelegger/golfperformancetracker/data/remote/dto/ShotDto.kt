package com.codelegger.golfperformancetracker.data.remote.dto

/**
 * Network representation of a single shot. [playerId] may be absent when shots are fetched
 * from a nested endpoint (e.g. /players/{id}/shots), so the mapper takes the id explicitly.
 */
data class ShotDto(
    val id: String,
    val playerId: String? = null,
    val ballSpeed: Double = 0.0,
    val launchAngle: Double = 0.0,
    val carryDistance: Double = 0.0,
    val clubType: String = "",
    val spinRate: Int = 0,
    val createdAt: String? = null,
)
