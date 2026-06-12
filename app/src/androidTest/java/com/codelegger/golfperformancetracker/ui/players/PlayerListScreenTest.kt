package com.codelegger.golfperformancetracker.ui.players

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.codelegger.golfperformancetracker.domain.model.Player
import com.codelegger.golfperformancetracker.theme.GolfTheme
import org.junit.Rule
import org.junit.Test

/** Verifies the stateless [PlayerListContent] renders players from the supplied state. */
class PlayerListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun playerList_rendersPlayerNameAndClub() {
        val state = PlayerListUiState(
            players = listOf(Player("1", "Jake Newman", "Driver", null, 156.0, 268.0)),
            isLoading = false,
        )
        composeTestRule.setContent {
            GolfTheme {
                PlayerListContent(uiState = state, onPlayerClick = {}, onRetry = {}, onQueryChange = {})
            }
        }

        composeTestRule.onNodeWithText("Jake Newman").assertIsDisplayed()
        composeTestRule.onNodeWithText("DRIVER").assertIsDisplayed()
    }
}
