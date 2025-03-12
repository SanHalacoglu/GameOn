package com.example.gameon

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

        Thread.sleep(3000)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra("DiscordLoginUrl", "https://discord.com/oauth2/authorize?client_id=1342993900419420181&redirect_uri=http://52.160.40.146:3000/auth/redirect&response_type=code&scope=identify+email+gdm.join+guilds.join")
        }

        val loginScenario = ActivityScenario.launch<LoginActivity>(intent)

        loginScenario.use {
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()
            Thread.sleep(3000)
            composeTestRule.onNodeWithTag("login_button").assertIsDisplayed().performClick()
            Thread.sleep(20000)

            val authorizeButton = device.findObject(UiSelector().text("Authorize"))
            if (authorizeButton.exists() && authorizeButton.isEnabled) {
                authorizeButton.click()
                Thread.sleep(20000)
            } else {
                throw AssertionError("Authorize button not found!")
            }
        }
    }

    @Test
    fun testFindGroupAfterLogin() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("FindGroupButton").assertIsDisplayed()
    }
}
