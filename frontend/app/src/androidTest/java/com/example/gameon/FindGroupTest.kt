package com.example.gameon

import android.content.Intent
import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

@RunWith(AndroidJUnit4::class)
class FindGroupTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(StartupActivity::class.java)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation())
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra("DiscordLoginUrl", "https://discord.com/oauth2/authorize?client_id=1342993900419420181&redirect_uri=http://52.160.40.146:3000/auth/redirect&response_type=code&scope=identify+email+gdm.join+guilds.join")
        }

        val loginScenario = ActivityScenario.launch<LoginActivity>(intent)
        loginScenario.use {
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()
            composeTestRule.onNodeWithTag("login_button").assertIsDisplayed().performClick()

            composeTestRule.waitUntil(timeoutMillis = 30_000) {
                device.findObject(UiSelector().text("Authorize")).exists()
            }

            val authorizeButton = device.findObject(UiSelector().text("Authorize"))
            if (authorizeButton.exists() && authorizeButton.isEnabled) {
                authorizeButton.click()
                composeTestRule.waitUntil(timeoutMillis = 10_000) {
                    device.currentPackageName == "com.example.gameon"
                }
            } else {
                throw AssertionError("Authorize button not found!")
            }
        }
    }

    @Test
    fun testFindGroupSuccess() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("FindGroupButton").assertIsDisplayed().performClick()
        composeTestRule.runOnIdle {
            Log.d("FindGroupTest", "Waiting for UI to recompose.")
        }
        composeTestRule.onNodeWithTag("FindGroupButton").assertTextContains("Finding...")

        composeTestRule.waitUntil(timeoutMillis = 120000) {
            composeTestRule.onAllNodesWithText("Group found! Navigating to group page.").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("MatchmakingPopup").assertIsDisplayed()

        composeTestRule.onNodeWithText("OK").assertIsDisplayed().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("MatchmakingPopup").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun testFindGroupFailure() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("FindGroupButton").assertIsDisplayed().performClick()
        composeTestRule.runOnIdle {
            Log.d("FindGroupTest", "Waiting for UI to recompose.")
        }
        composeTestRule.onNodeWithTag("FindGroupButton").assertTextContains("Finding...")

        composeTestRule.waitUntil(timeoutMillis = 120000) {
            composeTestRule.onAllNodesWithText("Matchmaking timed out. Please try again.").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("MatchmakingPopup").assertIsDisplayed()

        composeTestRule.onNodeWithText("OK").assertIsDisplayed().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithTag("MatchmakingPopup").fetchSemanticsNodes().isEmpty()
        }
    }
}
