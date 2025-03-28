package com.example.gameon.api.methods

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.PreferencesApi
import com.example.gameon.classes.Preferences

suspend fun updatePreferences(
    context: Context,
    preferenceId: Int,
    preferences: Preferences
): Boolean {
    try {
        if (preferenceId <= 0) {
            Log.e("updatePreferences", "Invalid preference ID: $preferenceId")
            return false
        }
        val apiService = Api.getInstance(context).create(PreferencesApi::class.java)
        val response = apiService.updatePreferences(preferenceId, preferences)

        return if (response.isSuccessful) {
            Log.d("updatePreferences", "Preferences updated successfully!")
            true
        } else {
            Log.e("updatePreferences", "Failed to update preferences: ${response.errorBody()?.string()}")
            false
        }
    } catch (e: Exception) {
        Log.e("updatePreferences", "Error updating preferences", e)
        return false
    }
}

suspend fun getPreferencesByUserId(context: Context, userId: String): Preferences? {
    val apiService = Api.getInstance(context, false)
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