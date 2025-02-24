package com.example.gameon.api.interfaces

import com.example.gameon.classes.Preferences
import retrofit2.http.Body
import retrofit2.http.POST

interface PreferencesApi {

    @POST("/preferences")
    suspend fun createPreferences(
        @Body preferences: Preferences
    )

}