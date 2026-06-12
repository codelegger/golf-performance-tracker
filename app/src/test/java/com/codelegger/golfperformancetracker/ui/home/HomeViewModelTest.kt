package com.codelegger.golfperformancetracker.ui.home

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Smoke test for the foundation's MVVM wiring: the ViewModel exposes a [HomeUiState]
 * with the expected default content. Uses Turbine to read from the [kotlinx.coroutines.flow.StateFlow].
 */
class HomeViewModelTest {

    @Test
    fun uiState_emitsDefaultContent() = runTest {
        val viewModel = HomeViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Golf Performance Tracker", state.title)
            assertEquals("Foundation ready", state.subtitle)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
