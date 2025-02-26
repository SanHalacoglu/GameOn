package com.example.gameon.api.methods

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gameon.MainActivity
import com.example.gameon.PreferencesActivity
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.PreferencesApi
import com.example.gameon.classes.Preferences
import retrofit2.Response

suspend fun createUserPreferences(
    context: Context,
    preferences: Preferences
) {
    val preferencesApi = Api.init(context)
        .getInstance(false)
        .create(PreferencesApi::class.java)

    val result: Response<Preferences> = preferencesApi.createPreferences(preferences)

    val intent: Intent

    if (result.isSuccessful) {
        Log.d("createUserPreferences", "Preferences created successfully: ${result.body()}")
        intent = Intent(context, MainActivity::class.java)
    } else {
        Log.e("createUserPreferences", "Failed to create preferences: ${result.errorBody()?.string()}")
        return
    }

    // Start the new activity and end the current one
    intent.let {
        context.startActivity(it)
        (context as? Activity)?.finish()
    }
}