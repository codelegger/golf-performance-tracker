package com.codelegger.golfperformancetracker.data.remote

import com.codelegger.golfperformancetracker.data.remote.dto.PlayerDto
import com.codelegger.golfperformancetracker.data.remote.dto.ShotDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * REST contract for the golf backend (MockAPI-compatible).
 *
 * Suspending functions integrate with coroutines; Retrofit + Moshi handle the JSON.
 */
interface GolfApi {

    @GET("players")
    suspend fun getPlayers(): List<PlayerDto>

    @GET("players/{playerId}/shots")
    suspend fun getShots(@Path("playerId") playerId: String): List<ShotDto>
}
