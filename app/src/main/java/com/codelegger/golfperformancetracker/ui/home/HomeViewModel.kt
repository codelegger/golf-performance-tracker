package com.codelegger.golfperformancetracker.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Immutable UI state for the Home screen, exposed as a single [StateFlow] — the
 * unidirectional-data-flow contract the rest of the app's screens will follow.
 */
data class HomeUiState(
    val title: String = "Golf Performance Tracker",
    val subtitle: String = "Foundation ready",
    val message: String = "Players, player detail, and shot metrics arrive in the next slices.",
)

/**
 * Placeholder Home ViewModel.
 *
 * It holds no business logic yet — its purpose in the foundation slice is to prove the
 * Hilt → ViewModel → StateFlow → Compose wiring compiles and works end to end.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
