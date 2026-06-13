package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.theme.clubColor
import com.codelegger.golfperformancetracker.theme.onClubColor
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Horizontal row of per-club summary pills (club badge + avg carry), like Rapsodo's
 * "8i 156 / 7i 105 / 4i 206" header. Aggregated from the player's shots.
 */
@Composable
fun StatPillRow(shots: List<Shot>, modifier: Modifier = Modifier) {
    val perClub = remember(shots) {
        shots.groupBy { it.clubType }
            .map { (club, list) -> club to list.map { it.carryDistance }.average() }
            .sortedByDescending { it.second }
    }
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(perClub, key = { it.first }) { (club, avgCarry) ->
            StatPill(club = club, value = avgCarry.roundToInt())
        }
    }
}

@Composable
private fun StatPill(club: String, value: Int) {
    val badgeColor = clubColor(club)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.surface)
            .padding(end = 12.dp, start = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(badgeColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = club.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = onClubColor(badgeColor),
            )
        }
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "yds",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Custom-drawn dispersion chart (Compose [Canvas]) — plots each shot by carry distance (x)
 * and launch angle (y), colored by club, on a dark panel with distance gridlines and a
 * dashed centerline. This is the "Custom Views for visualizing performance metrics" bonus.
 */
@Composable
fun ShotDispersionChart(shots: List<Shot>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val panel = Color(0xFF1A1A1A)
    val grid = Color(0xFF3D3D3D)
    val centerline = Color.White.copy(alpha = 0.4f)
    val labelStyle = TextStyle(color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)

    val maxCarry = remember(shots) {
        val m = shots.maxOfOrNull { it.carryDistance } ?: 100.0
        (ceil(m / 100.0) * 100.0).coerceAtLeast(100.0)
    }
    val minLaunch = remember(shots) { shots.minOfOrNull { it.launchAngle } ?: 0.0 }
    val maxLaunch = remember(shots) { shots.maxOfOrNull { it.launchAngle } ?: 1.0 }

    Column(modifier) {
        Text(
            text = "DISPERSION · carry (yds) × launch",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(panel)
                .padding(12.dp),
        ) {
            val w = size.width
            val h = size.height

            // Vertical distance gridlines + labels at each 100 yds.
            var yard = 100.0
            while (yard <= maxCarry) {
                val px = (yard / maxCarry * w).toFloat()
                drawLine(grid, Offset(px, 0f), Offset(px, h), strokeWidth = 1.dp.toPx())
                val label = textMeasurer.measure("${yard.roundToInt()}", labelStyle)
                drawText(label, topLeft = Offset(px - label.size.width / 2f, 0f))
                yard += 100.0
            }

            // Dashed centerline.
            drawLine(
                color = centerline,
                start = Offset(0f, h / 2f),
                end = Offset(w, h / 2f),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
            )

            // Shot dots: x = carry, y = launch angle (higher launch sits higher).
            val laRange = (maxLaunch - minLaunch).takeIf { it > 0.0 } ?: 1.0
            shots.forEach { shot ->
                val px = (shot.carryDistance / maxCarry * w).toFloat()
                val tNorm = (shot.launchAngle - minLaunch) / laRange
                val py = (h - (tNorm * (h * 0.7) + h * 0.15)).toFloat()
                drawCircle(
                    color = clubColor(shot.clubType),
                    radius = 6.dp.toPx(),
                    center = Offset(px, py),
                )
            }
        }
    }
}
