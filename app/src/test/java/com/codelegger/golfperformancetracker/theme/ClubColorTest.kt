package com.codelegger.golfperformancetracker.theme

import org.junit.Assert.assertEquals
import org.junit.Test

/** Verifies club labels (abbreviations or full names) resolve to a stable color. */
class ClubColorTest {

    @Test
    fun fullNameAndAbbreviation_resolveToSameColor() {
        assertEquals(clubColor("Driver"), clubColor("D"))
        assertEquals(clubColor("7 Iron"), clubColor("7i"))
        assertEquals(clubColor("Pitching Wedge"), clubColor("PW"))
    }

    @Test
    fun differentClubs_haveDifferentColors() {
        // 8i (red) vs 7i (pink) vs Driver (light blue) are distinct in Rapsodo's palette.
        val eight = clubColor("8i")
        val seven = clubColor("7i")
        val driver = clubColor("Driver")
        assertEquals(false, eight == seven)
        assertEquals(false, eight == driver)
    }

    @Test
    fun unknownClub_fallsBackInsteadOfCrashing() {
        // Should simply return the fallback color, not throw.
        clubColor("Frying Pan")
    }
}
