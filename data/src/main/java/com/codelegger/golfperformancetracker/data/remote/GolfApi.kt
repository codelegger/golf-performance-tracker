package com.codelegger.golfperformancetracker.data.remote

import com.codelegger.golfperformancetracker.data.remote.dto.PlayerDto
import com.codelegger.golfperformancetracker.data.remote.dto.ShotDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST contract for the golf backend (MockAPI-compatible).
 *
 * Suspending functions integrate with coroutines; Retrofit + Moshi handle the JSON.
 */
interface GolfApi {

    @GET("players")
    suspend fun getPlayers(): List<PlayerDto>

    /** Paged variant — MockAPI supports `?page=&limit=`. Used by Paging 3. */
    @GET("players")
    suspend fun getPlayersPaged(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): List<PlayerDto>

    @GET("players/{playerId}/shots")
    suspend fun getShots(@Path("playerId") playerId: String): List<ShotDto>
}
