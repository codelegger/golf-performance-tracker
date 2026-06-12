package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlayerDetailUiState(
    val player: Player? = null,
    val isLoading: Boolean = true,
)

/**
 * Reads a single player from the cache (SSOT) by the id passed as a navigation argument.
 * Hilt injects [SavedStateHandle], which carries the route's `{playerId}` — no manual
 * argument plumbing, and it survives process death.
 */
@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: PlayerRepository,
) : ViewModel() {

    private val playerId: String = checkNotNull(savedStateHandle[PLAYER_ID_ARG]) {
        "PlayerDetailViewModel requires a '$PLAYER_ID_ARG' navigation argument"
    }

    val uiState: StateFlow<PlayerDetailUiState> =
        repository.observePlayer(playerId)
            .map { player -> PlayerDetailUiState(player = player, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerDetailUiState(),
            )

    companion object {
        const val PLAYER_ID_ARG = "playerId"
    }
}
