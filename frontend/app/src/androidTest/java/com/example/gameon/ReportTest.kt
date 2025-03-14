package com.example.gameon

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ReportTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(StartupActivity::class.java)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val discordIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.com/oauth2/authorize?client_id=1342993900419420181&redirect_uri=http://52.160.40.146:3000/auth/redirect&response_type=code&scope=identify+email+gdm.join+guilds.join")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(discordIntent)
        clearDiscordCookies(device)

        val loginIntent = Intent(context, LoginActivity::class.java).apply {
            putExtra(
                "DiscordLoginUrl",
                "https://discord.com/oauth2/authorize?client_id=1342993900419420181&redirect_uri=http://52.160.40.146:3000/auth/redirect&response_type=code&scope=identify+email+gdm.join+guilds.join"
            )
        }
        val loginScenario = ActivityScenario.launch<LoginActivity>(loginIntent)

        loginScenario.use {
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()
            composeTestRule.onNodeWithTag("login_button").performClick()

            loginToDiscord(device)
        }

        device.waitForIdle()
        device.wait(Until.hasObject(By.text("Save password?")), 5000)

        device.wait(Until.hasObject(By.text("Never")), 5000)
        val neverButton = device.findObject(UiSelector().text("Never"))
        if (neverButton.exists() && neverButton.isEnabled) {
            neverButton.click()
        } else {
            Log.d("UiAutomator", "'Never' button not found. Moving on.")
        }

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

    @Test
    fun testReportSuccess() {
        // Load into Main Activity, click into Reports Activity via button
        composeTestRule.waitForIdle()
        val reportButton = composeTestRule.onNodeWithTag("ReportButton")
        composeTestRule.waitUntil {
            reportButton.isDisplayed()
        }
        reportButton.performClick()

        // Load into Reports Activity, check that submission is not yet possible
        composeTestRule.waitForIdle()
        val submitButton = composeTestRule.onNodeWithTag("SubmitReportButton")
        submitButton.assertIsDisplayed().assertIsNotEnabled()

        // Open Group dropdown, determine if dropdown values exist
        composeTestRule.onNodeWithTag("GroupDropdown").assertIsDisplayed().performClick()
        val firstGroup = composeTestRule.onNodeWithTag("Group_option_0")
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            firstGroup.isDisplayed()
        }
        
        // Select first dropdown value, ensure selection successful
        val firstGroupText = firstGroup.fetchSemanticsNode().config[SemanticsProperties.Text][0]
        firstGroup.performClick()
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            firstGroup.isNotDisplayed()
        }
        submitButton.assertIsNotEnabled()
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            firstGroupText.text == composeTestRule.onNodeWithTag("GroupTextField")
                .fetchSemanticsNode().config[SemanticsProperties.EditableText].text
        }


        // Open User dropdown, determine if dropdown values exist
        composeTestRule.onNodeWithTag("UserDropdown").assertIsDisplayed().performClick()
        val firstUser = composeTestRule.onNodeWithTag("User_option_0")
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            firstUser.isDisplayed()
        }

        // Select first user value, ensure selection successful
        val firstUserText = firstUser.fetchSemanticsNode().config[SemanticsProperties.Text][0]
        firstUser.performClick()
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            firstUser.isNotDisplayed()
        }
        submitButton.assertIsNotEnabled()
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            firstUserText.text == composeTestRule.onNodeWithTag("UserTextField")
                .fetchSemanticsNode().config[SemanticsProperties.EditableText].text
        }

        // Select the Reason input, input some text, check that submit button is now enabled
        val reasonInput = composeTestRule.onNodeWithTag("ReasonInput")
        reasonInput.assertIsDisplayed().performClick()
        composeTestRule.waitUntil {
            reasonInput.fetchSemanticsNode().config[SemanticsProperties.Focused]
        }
        reasonInput.performTextInput("This person is evil.")
        composeTestRule.waitUntil (timeoutMillis = 5000) {
            // Catching an assertion isn't normal
            // However there is no "SemanticsNodeInteraction.isEnabled()" function
            try {
                submitButton.assertIsEnabled()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        // Submit the report, confirm on main page, click into List Reports Activity
        submitButton.performClick()
        composeTestRule.waitForIdle()
        val viewReportsButton = composeTestRule.onNodeWithTag("ViewReportsButton")
        composeTestRule.waitUntil {
            viewReportsButton.isDisplayed()
        }
        viewReportsButton.performClick()

        // Ensure that the report list exists and that there is at least one report
        composeTestRule.waitForIdle()
        val reportList = composeTestRule.onNodeWithTag("ReportList")
        composeTestRule.waitUntil {
            reportList.isDisplayed() && reportList.fetchSemanticsNode().children.isNotEmpty()
        }

        // Get most recent report and assert that the reported user is in the list
        val recentReport = reportList
            .onChildAt(reportList.fetchSemanticsNode().children.size - 1)
        assert(firstUserText in recentReport.onChildAt(0)
            .fetchSemanticsNode().config[SemanticsProperties.Text])

        // Return to main page and ensure you are in the main page
        composeTestRule.onNodeWithTag("ListReportsBackButton")
            .assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil {
            reportButton.isDisplayed()
        }
    }

    @Test
    fun testCancelReportFailure() {
        // Load into Main Activity, click into Reports Activity via button
        composeTestRule.waitForIdle()
        val reportButton = composeTestRule.onNodeWithTag("ReportButton")
        composeTestRule.waitUntil {
            reportButton.isDisplayed()
        }
        reportButton.performClick()

        // Load into Reports Activity, check that submission is not yet possible
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("SubmitReportButton")
            .assertIsDisplayed().assertIsNotEnabled()

        // Click the "Cancel" button
        composeTestRule.onNodeWithTag("CancelReportButton")
            .assertIsDisplayed().performClick()

        // Confirm test is back on the main page
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil {
            reportButton.isDisplayed()
        }
    }
}