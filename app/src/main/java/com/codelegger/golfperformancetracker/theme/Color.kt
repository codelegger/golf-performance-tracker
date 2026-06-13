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

// ── Club colours — matched to Rapsodo's club palette ───────────────────────
// Keyed by a normalized club code so both "Driver"/"D" and "7 Iron"/"7i" resolve.
private val ClubColors: Map<String, Color> = mapOf(
    "d" to Color(0xFF9ED2E8),   // Driver – light blue
    "3w" to Color(0xFF2D7DD2),  // blue
    "5w" to Color(0xFF1B3FA0),  // navy
    "ot" to Color(0xFF9E9E9E),  // other – gray
    "3h" to Color(0xFF2E9E86),  // teal
    "4h" to Color(0xFF44C281),  // green
    "5h" to Color(0xFF2FB36B),  // green
    "4i" to Color(0xFFE0901E),  // orange
    "5i" to Color(0xFFE3C72E),  // yellow
    "6i" to Color(0xFFD9B48F),  // tan
    "7i" to Color(0xFFE89AA4),  // pink
    "8i" to Color(0xFFD7414A),  // red
    "9i" to Color(0xFF9B4D9E),  // magenta
    "pw" to Color(0xFFC9B3E6),  // lavender
    "gw" to Color(0xFFB07FE0),  // light purple
    "sw" to Color(0xFF7A4FBF),  // purple
    "54" to Color(0xFF8E5AD6),  // purple
    "lw" to Color(0xFF5E2CA5),  // deep purple
)

private val ClubFallback = Color(0xFF9E9E9E)

/** Normalizes a club label ("7 Iron", "7i", "Driver", "Pitching Wedge") to a lookup code. */
internal fun normalizeClub(raw: String): String {
    val s = raw.lowercase().replace(" ", "")
    return when {
        s.startsWith("driver") || s == "d" -> "d"
        s.contains("pitching") || s == "pw" -> "pw"
        s.contains("gap") || s == "gw" -> "gw"
        s.contains("sand") || s == "sw" -> "sw"
        s.contains("lob") || s == "lw" -> "lw"
        Regex("^\\d+iron$").matches(s) -> s.removeSuffix("iron") + "i"
        else -> s
    }
}

/** Rapsodo-style category color for a club, consistent for the same club. */
fun clubColor(club: String): Color = ClubColors[normalizeClub(club)] ?: ClubFallback

/** Readable text color on a colored badge/avatar: dark ink on light fills, white on dark. */
fun onClubColor(background: Color): Color =
    if (background.luminance() > 0.6f) Ink900 else Color.White

// ── Player avatar palette — a distinct, stable color per person ────────────
private val PersonColors = listOf(
    Color(0xFF4C6EF5), // indigo
    Color(0xFF12B886), // teal
    Color(0xFFE8590C), // orange
    Color(0xFFAE3EC9), // purple
    Color(0xFF1098AD), // cyan
    Color(0xFFE64980), // pink
    Color(0xFF66A80F), // lime
    Color(0xFFF59F00), // amber
)

/** Deterministic avatar color for a person, so the same name is always the same color. */
fun personColor(name: String): Color =
    PersonColors[name.lowercase().hashCode().absoluteValue % PersonColors.size]
