package com.codelegger.golfperformancetracker.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.absoluteValue

// ── Brand & neutrals (DESIGN_LANGUAGE tokens) ──────────────────────────────
val BrandRed = Color(0xFFD7263D)
val BrandRedPressed = Color(0xFFB01E32)
val Ink900 = Color(0xFF1C1C1E)
val Ink700 = Color(0xFF3A3A3C)
val Ink500 = Color(0xFF6E6E73)
val Ink300 = Color(0xFF9E9E9E)
val DividerLine = Color(0xFFE5E5EA)
val Surface0 = Color(0xFFFFFFFF)
val Surface50 = Color(0xFFF7F7F8)

// Dark scheme neutrals
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1C1C1E)
val DarkSurfaceVariant = Color(0xFF2A2A2C)
val DarkOnSurface = Color(0xFFECECEC)
val DarkOnSurfaceVariant = Color(0xFFB0B0B5)
val DarkOutline = Color(0xFF3D3D3D)
val BrandRedLight = Color(0xFFFF5A6E) // brand red lightened for dark backgrounds

// ── Category (club) scale — color ONLY used for club badges ────────────────
private val CategoryColors = listOf(
    Color(0xFFD7414A), // red
    Color(0xFFE59AA4), // pink
    Color(0xFFE0A82E), // gold
    Color(0xFF9B4D9E), // magenta
    Color(0xFFB7A4DC), // lavender
    Color(0xFFC9B3E6), // lilac
    Color(0xFF6A1B9A), // purple
)

/** Deterministic category color for a club, so the same club is always the same color. */
fun clubColor(club: String): Color =
    CategoryColors[club.lowercase().hashCode().absoluteValue % CategoryColors.size]

/** Readable text color on a club badge: dark ink on light fills, white on dark fills. */
fun onClubColor(background: Color): Color =
    if (background.luminance() > 0.6f) Ink900 else Color.White
