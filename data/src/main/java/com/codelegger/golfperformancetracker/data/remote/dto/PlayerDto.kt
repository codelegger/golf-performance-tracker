package com.codelegger.golfperformancetracker.data.remote.dto

import com.squareup.moshi.Json

/**
 * Network representation of a player, as returned by the REST API.
 *
 * Kept separate from the domain [com.codelegger.golfperformancetracker.domain.model.Player]
 * so the API's shape can change without rippling through the app. Defaults make parsing
 * resilient to missing/optional fields from the mock backend.
 */
data class PlayerDto(
    val id: String,
    val name: String,
    val club: String,
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    val averageBallSpeed: Double = 0.0,
    val averageCarryDistance: Double = 0.0,
)
