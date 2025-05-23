package com.example.gameon.api.methods

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.gameon.BannedActivity
import com.example.gameon.LoginActivity
import com.example.gameon.MainActivity
import com.example.gameon.PreferencesActivity
import com.example.gameon.StartupActivity
import com.example.gameon.api.Api
import com.example.gameon.api.PersistentCookieJar
import com.example.gameon.api.interfaces.AuthApi
import com.example.gameon.classes.Preferences

suspend fun checkLoggedIn (context: Context) {
    val authApi = Api.getInstance(context, false)
        .create(AuthApi::class.java)
    val result = authApi.login()

    val sessionDetails = SessionDetails(context)
    val intent: Intent = if (result.isSuccessful) {
        val user = result.body()
        if (user != null && !user.banned) {
            // Save user information
            sessionDetails.saveUser(user)
            // Save admin ID
            val admins = getAdmins(context)
            val admin = admins.find {
                    a ->
                a.discord_id == user.discord_id ||
                        a.user?.discord_id == user.discord_id
            }
            sessionDetails.saveAdminId(admin?.admin_id)

            Intent(context, MainActivity::class.java)
        } else if (user != null) {
            Intent(context, BannedActivity::class.java)
        } else {
            Intent(context, MainActivity::class.java)
        }
    }

    //Upon redirect, redirect to either Login or Preferences pages
    else if (result.code() in 300..399) {
        val redirectUrl: String = result.headers().get("Location")!!
        Log.d("Auth", "Redirect URL: $redirectUrl")
        if (redirectUrl.startsWith("https://discord.com")) {
            Intent(context, LoginActivity::class.java).apply {
                putExtra("DiscordLoginUrl", redirectUrl)
            }
        } else {
            val discordId = Uri.parse(redirectUrl).getQueryParameter("discord_id")!!
            Intent(context, PreferencesActivity::class.java).apply {
                putExtra("DiscordId", discordId)
            }
        }
    }
    // Handle errors
    else {
        return
    }

    // Start the new activity and end the current one
    intent.let {
        context.startActivity(it)
        (context as? Activity)?.finish()
    }
}

suspend fun finishLogin (
    code: String,
    context: Context,
) {
    val authApi = Api.getInstance(context, false).create(AuthApi::class.java)
    val result = authApi.discordCallback(code)
    val sessionDetails = SessionDetails(context)

    // If successful continue
    val intent: Intent = if (result.isSuccessful) {
        val user = result.body()
        if (user != null && !user.banned) {
            // Save user information
            sessionDetails.saveUser(user)
            // Save admin ID
            val admins = getAdmins(context)
            val admin = admins.find {
                    a ->
                a.discord_id == user.discord_id ||
                        a.user?.discord_id == user.discord_id
            }
            sessionDetails.saveAdminId(admin?.admin_id)

            Intent(context, MainActivity::class.java)
        } else if (user != null) {
            Intent(context, BannedActivity::class.java)
        } else {
            Intent(context, MainActivity::class.java)
        }
    }
    // Upon redirect, redirect to Preferences page
    else if (result.code() in 300..399) {
        val redirectUrl: String = result.headers().get("Location")!!
        val discordId = Uri.parse(redirectUrl).getQueryParameter("discord_id")!!
        Intent(context, PreferencesActivity::class.java).apply {
            putExtra("DiscordId", discordId)
        }
    }
    // Handle errors
    else {
        return
    }

    // Start the new activity and end the current one
    intent.let {
        context.startActivity(it)
        (context as? Activity)?.finish()
    }
}

suspend fun register(
    context: Context,
    preferences: Preferences
) {
    val authApi = Api.getInstance(context, false)
        .create(AuthApi::class.java)

    val result = authApi.register(preferences)
    val sessionDetails = SessionDetails(context)

    val intent: Intent = if (result.isSuccessful) {
        val user = result.body()
        if (user != null) {
            sessionDetails.saveUser(user)
            // User will not be an admin upon registering
            sessionDetails.saveAdminId(null)
        }
        Log.d("Auth", "Preferences created successfully: $user")
        Intent(context, MainActivity::class.java)
    } else if (result.code() in 300..399) {
        val redirectUrl: String = result.headers().get("Location")!!
        Log.d("Auth", "Redirect URL: $redirectUrl")
        Intent(context, LoginActivity::class.java).apply {
            putExtra("DiscordLoginUrl", redirectUrl)
        }
    } else {
        Log.e("Auth", "Failed to create preferences: ${result.errorBody()?.string()}")
        return
    }
    // Start the new activity and end the current one
    intent.let {
        context.startActivity(it)
        (context as? Activity)?.finish()
    }
}

suspend fun logout(context: Context) {
    val authApi = Api.getInstance(context, false)
        .create(AuthApi::class.java)

    val result = authApi.logout()

    if (result.isSuccessful) {
        val sessionDetails = SessionDetails(context)
        sessionDetails.clearUser()

        PersistentCookieJar(context).clear()

        context.startActivity(Intent(context, StartupActivity::class.java))
        (context as? Activity)?.finish()
    }
}