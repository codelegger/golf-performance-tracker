package com.codelegger.golfperformancetracker.ui.players.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.model.Shot
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import com.codelegger.golfperformancetracker.domain.repository.ShotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    val isRefreshing: Boolean = false,
    /** True when the latest shot refresh failed — cached shots are shown but may be stale. */
    val refreshFailed: Boolean = false,
)

/**
 * Reads a single player and their shots from the cache (SSOT), keyed by the `{playerId}`
 * navigation argument. On open it kicks off a shots refresh from the network; the cached
 * shots are shown immediately and update reactively once the refresh lands.
 *
 * Stays offline-first — a failed refresh never hides cached data — but, unlike before, the
 * failure is no longer swallowed: it surfaces as [PlayerDetailUiState.refreshFailed] so the
 * screen can show a stale-data notice and a [refresh] retry affordance.
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

    private val isRefreshing = MutableStateFlow(false)
    private val refreshFailed = MutableStateFlow(false)

    val uiState: StateFlow<PlayerDetailUiState> =
        combine(
            playerRepository.observePlayer(playerId),
            shotRepository.observeShots(playerId),
            isRefreshing,
            refreshFailed,
        ) { player, shots, refreshing, failed ->
            PlayerDetailUiState(
                player = player,
                shots = shots,
                isLoading = false,
                isRefreshing = refreshing,
                refreshFailed = failed,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerDetailUiState(),
        )

    init {
        refresh()
    }

    /**
     * Refreshes this player's shots from the network. Cached shots stay visible throughout;
     * a failure flips [PlayerDetailUiState.refreshFailed] so the UI can offer a retry.
     */
    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            refreshFailed.value = false
            shotRepository.refreshShots(playerId).onFailure { refreshFailed.value = true }
            isRefreshing.value = false
        }
    }

    companion object {
        const val PLAYER_ID_ARG = "playerId"
    }
}
