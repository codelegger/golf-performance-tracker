package com.codelegger.golfperformancetracker.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.domain.repository.PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Immutable UI state for the player list.
 *
 * Modeled as a single data class (not a sealed hierarchy) precisely because this is
 * offline-first: we may need to show cached [players] *and* a transient refresh [errorMessage]
 * at the same time — a sealed Loading/Success/Error wouldn't express that combination.
 */
data class PlayerListUiState(
    val players: List<Player> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class PlayerListViewModel @Inject constructor(
    private val repository: PlayerRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    /**
     * Combines the reactive cache stream with transient UI signals into one state. Because
     * the players come from Room, the list survives config changes and shows instantly offline.
     */
    val uiState: StateFlow<PlayerListUiState> =
        combine(
            repository.observePlayers(),
            isRefreshing,
            errorMessage,
        ) { players, refreshing, error ->
            PlayerListUiState(
                players = players,
                isLoading = false,
                isRefreshing = refreshing,
                errorMessage = error,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerListUiState(),
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            errorMessage.value = null
            repository.refreshPlayers().onFailure { throwable ->
                errorMessage.value = throwable.message ?: "Couldn't refresh players"
            }
            isRefreshing.value = false
        }
    }

    /** Call after a transient error has been shown to the user (e.g. snackbar dismissed). */
    fun onErrorShown() {
        errorMessage.value = null
    }
}
