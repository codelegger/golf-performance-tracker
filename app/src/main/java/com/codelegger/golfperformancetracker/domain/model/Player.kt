package com.codelegger.golfperformancetracker.domain.model

/**
 * A golf player as the app domain understands it — free of any network (DTO) or
 * database (Entity) concerns. This is the type the UI and ViewModels work with.
 */
data class Player(
    val id: String,
    val name: String,
    val club: String,
    val avatarUrl: String?,
    val averageBallSpeed: Double,
    val averageCarryDistance: Double,
)
