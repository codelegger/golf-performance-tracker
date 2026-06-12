package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.theme.GolfTheme

@Composable
fun PlayerDetailScreen(
    onBack: () -> Unit,
    viewModel: PlayerDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PlayerDetailContent(uiState = uiState, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerDetailContent(
    uiState: PlayerDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text((uiState.player?.name ?: "PLAYER").uppercase()) },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading ->
                Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                    CircularProgressIndicator()
                }

            uiState.player == null ->
                Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                    Text("Player not found.")
                }

            else -> PlayerDetailBody(
                player = uiState.player,
                shots = uiState.shots,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun PlayerDetailBody(
    player: Player,
    shots: List<Shot>,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 24.dp,
            bottom = 24.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                InitialsAvatar(name = player.name, size = 88)
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = player.club.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HeroMetric("AVG BALL SPEED", "%.0f".format(player.averageBallSpeed), "MPH")
                HeroMetric("AVG CARRY", "%.0f".format(player.averageCarryDistance), "YARDS")
            }
        }

        item {
            Text(
                text = "SHOTS (${shots.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        if (shots.isEmpty()) {
            item {
                Text(
                    text = "No shots recorded yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            items(shots, key = { it.id }) { shot -> ShotCard(shot) }
        }
    }
}

@Composable
private fun ShotCard(shot: Shot) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = shot.clubType,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ShotMetric("BALL", "%.0f".format(shot.ballSpeed), "mph")
                ShotMetric("LAUNCH", "%.1f".format(shot.launchAngle), "°")
                ShotMetric("CARRY", "%.0f".format(shot.carryDistance), "yd")
                ShotMetric("SPIN", "${shot.spinRate}", "rpm")
            }
        }
    }
}

@Composable
private fun ShotMetric(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** A single big number with a quiet uppercase label and unit — the data-forward hero block. */
@Composable
private fun HeroMetric(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InitialsAvatar(name: String, size: Int) {
    val initials = name.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
    Box(
        modifier = Modifier.size(size.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerDetailPreview() {
    GolfTheme {
        PlayerDetailContent(
            uiState = PlayerDetailUiState(
                player = Player("1", "Jake Newman", "Driver", null, 167.0, 291.0),
                shots = listOf(
                    Shot("s1", "1", 168.0, 11.4, 293.0, "Driver", 2480, null),
                    Shot("s2", "1", 118.6, 19.5, 171.0, "7i", 6700, null),
                ),
                isLoading = false,
            ),
            onBack = {},
        )
    }
}
