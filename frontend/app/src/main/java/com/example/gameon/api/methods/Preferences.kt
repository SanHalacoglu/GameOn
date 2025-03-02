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

suspend fun getUserPreferences(preferenceId: Int, context: Context): Preferences? {
    val apiService = Api.init(context)
        .getInstance(followRedirects = false)
        .create(PreferencesApi::class.java)

    val response = apiService.getPreferencesById(preferenceId)

    return if (response.isSuccessful) {
        response.body()
    } else {
        Log.e("UserSettings", "Failed to fetch preferences: ${response.errorBody()?.string()}")
        null
    }
}

suspend fun updatePreferences(context: Context, preferenceId: Int, preferences: Preferences): Boolean {
    return try {
        val apiService = Api.init(context).getInstance().create(PreferencesApi::class.java)
        val response = apiService.updatePreferences(preferenceId, preferences)

        if (response.isSuccessful) {
            Log.d("updatePreferences", "Preferences updated successfully!")
            true
        } else {
            Log.e("updatePreferences", "Failed to update preferences: ${response.errorBody()?.string()}")
            false
        }
    } catch (e: Exception) {
        Log.e("updatePreferences", "Error updating preferences", e)
        false
    }
}

suspend fun getPreferencesByUserId(context: Context, userId: String): Preferences? {
    val apiService = Api.init(context)
        .getInstance(followRedirects = false)
        .create(PreferencesApi::class.java)

    val response = apiService.getPreferencesByUserId(userId)

    return if (response.isSuccessful) {
        Log.d("getPreferencesByUserId", "Fetched preferences: ${response.body()}")
        response.body()
    } else {
        Log.e("getPreferencesByUserId", "Failed to fetch preferences: ${response.errorBody()?.string()}")
        null
    }
}