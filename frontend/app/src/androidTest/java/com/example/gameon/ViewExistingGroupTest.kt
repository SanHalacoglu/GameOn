package com.example.gameon

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import com.example.gameon.classes.Group
import com.example.gameon.classes.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewExistingGroupTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testViewExistingGroupSuccess() {

        activityRule.scenario.onActivity { activity ->
            activity.getGroupListState().value = listOf(
                Group(
                    group_name = "Test Group 1",
                    group_id = 1,
                    max_players = 5,
                    game_id = 3,
                ),
                Group(
                    group_name = "Test Group 2",
                    group_id = 2,
                    max_players = 3,
                    game_id = 2,
                )
            )
        }

        composeTestRule.onNodeWithTag("Group:Test Group 1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Group:Test Group 2").assertIsDisplayed()

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ViewGroupActivity::class.java
        ).apply {
            putExtra("selected_group", Group(group_name = "Test Group 1", group_id = 1, max_players = 5, game_id = 3))
            putExtra("discord_username", "TestUser")
        }

        val viewGroupScenario = ActivityScenario.launch<ViewGroupActivity>(intent)
        viewGroupScenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.getGroupMemberState().value = listOf(
                    User(discord_id = "123", username = "TestUser1", email = "user1@gmail.com", banned = false),
                    User(discord_id = "456", username = "TestUser2", email = "user2@gmail.com", banned = false)
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNodeWithTag("GroupMember:TestUser1").assertExists()
            true
        }

        composeTestRule.onNodeWithTag("GroupMember:TestUser1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GroupMember:TestUser2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GroupName:Test Group 1").assertIsDisplayed()
    }

    @Test
    fun testViewExistingGroupFailure() {

        activityRule.scenario.onActivity { activity ->
            activity.getGroupListState().value = listOf(
                Group(
                    group_name = "Test Group 1",
                    group_id = 1,
                    max_players = 5,
                    game_id = 3,
                ),
                Group(
                    group_name = "Test Group 2",
                    group_id = 2,
                    max_players = 3,
                    game_id = 2,
                )
            )
        }

        composeTestRule.onNodeWithTag("Group:Test Group 1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Group:Test Group 2").assertIsDisplayed()

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ViewGroupActivity::class.java
        ).apply {
            putExtra("selected_group", Group(group_name = "Test Group 1", group_id = 1, max_players = 5, game_id = 3))
            putExtra("discord_username", "TestUser")
        }

        val viewGroupScenario = ActivityScenario.launch<ViewGroupActivity>(intent)
        viewGroupScenario.onActivity { activity ->
            activity.runOnUiThread {
                activity.getGroupMemberState().value = emptyList()
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNodeWithTag("GroupMembersErrorPopup").assertExists()
            true
        }

        composeTestRule.onNodeWithTag("GroupMembersErrorPopup").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GroupMember:TestUser1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("GroupMember:TestUser2").assertDoesNotExist()
    }
}