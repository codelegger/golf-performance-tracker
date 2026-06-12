package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
        val content = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when {
            uiState.isLoading -> Box(content, Alignment.Center) { CircularProgressIndicator() }
            uiState.player == null -> Box(content, Alignment.Center) { Text("Player not found.") }
            else -> PlayerDetailBody(player = uiState.player, modifier = content)
        }
    }
}

@Composable
private fun PlayerDetailBody(player: Player, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HeroMetric(
                label = "AVG BALL SPEED",
                value = "%.0f".format(player.averageBallSpeed),
                unit = "MPH",
            )
            HeroMetric(
                label = "AVG CARRY",
                value = "%.0f".format(player.averageCarryDistance),
                unit = "YARDS",
            )
        }

        Text(
            text = "Shot-by-shot metrics coming next.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 32.dp),
        )
    }
}

/** A single big number with a quiet uppercase label and unit — the data-forward hero block. */
@Composable
private fun HeroMetric(label: String, value: String, unit: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InitialsAvatar(name: String, size: Int) {
    val initials = name.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
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
                isLoading = false,
            ),
            onBack = {},
        )
    }
}
