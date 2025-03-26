package com.example.gameon.api.interfaces

import com.example.gameon.classes.Preferences
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PreferencesApi {

    @PUT("/preferences/{id}")
    suspend fun updatePreferences(
        @Path("id") preferenceId: Int,
        @Body preferences: Preferences
    ): Response<Preferences>

    @GET("/preferences/user/{userId}")
    suspend fun getPreferencesByUserId(
        @Path("userId") userId: String
    ): Response<Preferences>

}