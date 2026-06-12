package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerDetailUiState(
    val player: Player? = null,
    val shots: List<Shot> = emptyList(),
    val isLoading: Boolean = true,
)

/**
 * Reads a single player and their shots from the cache (SSOT), keyed by the `{playerId}`
 * navigation argument. On open it kicks off a shots refresh from the network; the cached
 * shots are shown immediately and update reactively once the refresh lands.
 */
@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    playerRepository: PlayerRepository,
    private val shotRepository: ShotRepository,
) : ViewModel() {

    private val playerId: String = checkNotNull(savedStateHandle[PLAYER_ID_ARG]) {
        "PlayerDetailViewModel requires a '$PLAYER_ID_ARG' navigation argument"
    }

    val uiState: StateFlow<PlayerDetailUiState> =
        combine(
            playerRepository.observePlayer(playerId),
            shotRepository.observeShots(playerId),
        ) { player, shots ->
            PlayerDetailUiState(player = player, shots = shots, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerDetailUiState(),
        )

    init {
        // Best-effort refresh; cached shots are already shown, so failure is non-fatal here.
        viewModelScope.launch { shotRepository.refreshShots(playerId) }
    }

    companion object {
        const val PLAYER_ID_ARG = "playerId"
    }
}
