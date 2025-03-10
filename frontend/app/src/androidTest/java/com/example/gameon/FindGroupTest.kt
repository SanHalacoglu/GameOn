package com.example.gameon

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import com.example.gameon.classes.Group
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindGroupTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Reset activity state before each test
        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.getMatchmakingStatus().value = null
                activity.getIsMatchmakingActive().value = false
                activity.getDialogMessage().value = ""
                activity.getShowDialog().value = false
                activity.getGroupListState().value = emptyList()
            }
        }
        Thread.sleep(500)
    }

    @Test
    fun testFindGroupSuccess() {
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onNodeWithTag("FindGroupButton").assertExists()
            true
        }
        composeTestRule.onNodeWithTag("FindGroupButton").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("FindGroupButton").performClick()
        composeTestRule.onNodeWithText("Finding...").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FindGroupButton").assertIsNotEnabled()

        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.getMatchmakingStatus().value = "group_found"
                activity.getIsMatchmakingActive().value = false
                activity.getDialogMessage().value = "You have been matched with a group!"
                activity.getShowDialog().value = true

                activity.getGroupListState().value = listOf(
                    Group(group_name = "New Matchmaking Group", max_players = 3, game_id = 1)
                )
            }
        }

        composeTestRule.onNodeWithTag("MatchmakingPopup").assertIsDisplayed()
        composeTestRule.onNodeWithText("You have been matched with a group!").assertIsDisplayed()

        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithTag("MatchmakingPopup").assertDoesNotExist()
        composeTestRule.onNodeWithTag("Group:New Matchmaking Group").assertIsDisplayed()
    }

    @Test
    fun testFindGroupFailure() {
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onNodeWithTag("FindGroupButton").assertExists()
            true
        }
        composeTestRule.onNodeWithTag("FindGroupButton").performClick()
        composeTestRule.onNodeWithText("Finding...").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FindGroupButton").assertIsNotEnabled()

        activityRule.scenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.getMatchmakingStatus().value = "timed_out"
                activity.getIsMatchmakingActive().value = false
                activity.getDialogMessage().value = "Matchmaking timed out. Please try again."
                activity.getShowDialog().value = true
            }
        }

        composeTestRule.onNodeWithTag("MatchmakingPopup").assertIsDisplayed()
        composeTestRule.onNodeWithText("Matchmaking timed out. Please try again.").assertIsDisplayed()
    }
}