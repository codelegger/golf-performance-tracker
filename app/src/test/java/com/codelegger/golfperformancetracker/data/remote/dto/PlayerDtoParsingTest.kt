package com.codelegger.golfperformancetracker.data.remote.dto

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Verifies the Moshi adapters parse the API JSON into DTOs correctly, including optional
 * fields. This is the same Moshi configuration the app wires up in NetworkModule.
 */
class PlayerDtoParsingTest {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Test
    fun parsesFullPlayerJson() {
        val json = """
            {
              "id": "1",
              "name": "Jake Newman",
              "club": "Driver",
              "avatarUrl": "https://img.example/jake.png",
              "averageBallSpeed": 156.3,
              "averageCarryDistance": 268.0
            }
        """.trimIndent()

        val dto = moshi.adapter(PlayerDto::class.java).fromJson(json)!!

        assertEquals("1", dto.id)
        assertEquals("Jake Newman", dto.name)
        assertEquals("Driver", dto.club)
        assertEquals("https://img.example/jake.png", dto.avatarUrl)
        assertEquals(156.3, dto.averageBallSpeed, 0.001)
        assertEquals(268.0, dto.averageCarryDistance, 0.001)
    }

    @Test
    fun appliesDefaultsForMissingOptionalFields() {
        val json = """{ "id": "2", "name": "Sam Lee", "club": "7i" }"""

        val dto = moshi.adapter(PlayerDto::class.java).fromJson(json)!!

        assertNull(dto.avatarUrl)
        assertEquals(0.0, dto.averageBallSpeed, 0.0)
        assertEquals(0.0, dto.averageCarryDistance, 0.0)
    }
}
