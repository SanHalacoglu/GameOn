package com.example.gameon.api.methods

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gameon.LoginActivity
import com.example.gameon.MainActivity
import com.example.gameon.PreferencesActivity
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.AuthApi

suspend fun checkLoggedIn (
    context: Context,
) {
    val authApi = Api.init(context)
        .getInstance(false)
        .create(AuthApi::class.java)

    val result = authApi.login()

    val intent: Intent

    //If successful continue
    if (result.isSuccessful) {
        intent = Intent(context, MainActivity::class.java)
    }
    //Upon redirect, redirect to either Login or Preferences pages
    else if (result.code() in 300..399) {
        val redirectUrl: String = result.headers().get("Location")!!

        if (redirectUrl.startsWith("https://discord.com")) {
            intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("DiscordLoginUrl", redirectUrl)
        } else {
            intent = Intent(context, PreferencesActivity::class.java)
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
    val authApi = Api.init(context).getInstance(false).create(AuthApi::class.java)
    val result = authApi.discordCallback(code)

    // If successful continue
    val intent: Intent = if (result.isSuccessful) {
        Log.d("Auth", result.body().toString())
        Intent(context, MainActivity::class.java)
    }
    // Upon redirect, redirect to Preferences page
    else if (result.code() in 300..399) {
        Intent(context, PreferencesActivity::class.java)
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