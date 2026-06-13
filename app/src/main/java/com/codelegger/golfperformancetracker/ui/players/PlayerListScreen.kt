package com.codelegger.golfperformancetracker.ui.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.ui.components.PlayerAvatar

/**
 * Player list backed by Paging 3 ([collectAsLazyPagingItems]). The combined load state drives
 * the initial spinner / error retry, and an append spinner shows while the next page loads.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    onPlayerClick: (String) -> Unit,
    viewModel: PlayerListViewModel = hiltViewModel(),
) {
    val players = viewModel.players.collectAsLazyPagingItems()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(title = { Text("PLAYERS") }) },
    ) { innerPadding ->
        when (val refresh = players.loadState.refresh) {
            is LoadState.Loading -> CenterBox(innerPadding) { CircularProgressIndicator() }

            is LoadState.Error -> CenterBox(innerPadding) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (refresh.error as? java.io.IOException)
                            ?.let { "No connection. Tap retry." }
                            ?: "Couldn't load players.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    TextButton(onClick = { players.retry() }) { Text("Retry") }
                }
            }

            else -> LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(players.itemCount, key = players.itemKey { it.id }) { index ->
                    players[index]?.let { player ->
                        PlayerCard(player = player, onClick = { onPlayerClick(player.id) })
                    }
                }
                if (players.loadState.append is LoadState.Loading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterBox(padding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun PlayerCard(player: Player, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerAvatar(name = player.name, imageUrl = player.avatarUrl, size = 48)
            Column(
                modifier = Modifier.weight(1f).padding(start = 16.dp),
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = player.club.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%.0f".format(player.averageBallSpeed),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "MPH AVG",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
