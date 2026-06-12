package com.codelegger.golfperformancetracker.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.codelegger.golfperformancetracker.theme.GolfTheme
import org.junit.Rule
import org.junit.Test

/** Verifies the stateless [HomeContent] renders the supplied UI state. */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun homeContent_showsTitleAndSubtitle() {
        val state = HomeUiState()
        composeTestRule.setContent {
            GolfTheme { HomeContent(uiState = state) }
        }

        composeTestRule.onNodeWithText(state.title).assertIsDisplayed()
        composeTestRule.onNodeWithText(state.subtitle).assertIsDisplayed()
    }
}
