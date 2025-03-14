package com.example.gameon

import android.util.Log
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun clearDiscordCookies(device: UiDevice) {
    try {
        val connectionSecure = device.wait(Until.findObject(By.descContains("Connection is secure")), 20_000)
        if (connectionSecure == null) {
            throw AssertionError("Page did not load: 'Connection is secure' not found!")
        }
        connectionSecure.click()
        Thread.sleep(1000)

        device.findObject(UiSelector().textContains("Cookies")).click()
        Thread.sleep(1000)

        device.findObject(UiSelector().descriptionContains("Clear cookies")).click()
        Thread.sleep(1000)

        device.findObject(UiSelector().text("Clear")).click()
        Thread.sleep(1000)

        Log.d("FindGroupTest", "Successfully cleared Discord cookies!")

    } catch (e: Exception) {
        Log.e("FindGroupTest", "Failed to clear Discord cookies", e)
    }
}

fun loginToDiscord(device: UiDevice) {
    val username = "gameontestuser@outlook.com"
    val password = "Cpen321!isfun"

    try {
        device.waitForIdle()
        Thread.sleep(15000)

        val usernameField = device.findObject(UiSelector().className("android.widget.EditText").instance(0))
        usernameField.setText(username)

        Thread.sleep(1000)

        val passwordField = device.findObject(UiSelector().className("android.widget.EditText").instance(1))
        if (passwordField.exists() && passwordField.isEnabled) {
            passwordField.click()
            Thread.sleep(500)
            passwordField.setText(password)
        } else {
            throw AssertionError("Password field not found!")
        }

        Thread.sleep(1000)

        val loginButton = device.findObject(UiSelector().className("android.widget.Button").text("Log In"))
        if (loginButton.exists() && loginButton.isEnabled) {
            loginButton.click()
        } else {
            throw AssertionError("Login button not found!")
        }

        Log.d("UiAutomator", "Successfully submitted Discord login form.")

    } catch (e: Exception) {
        Log.e("UiAutomator", "Failed to log into Discord!", e)
    }
}