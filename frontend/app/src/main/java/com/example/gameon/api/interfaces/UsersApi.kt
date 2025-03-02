package com.example.gameon.api.interfaces

import com.example.gameon.classes.Group
import com.example.gameon.classes.Preferences
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsersApi {

    @GET("/users/{id}/groups")
    suspend fun getUserGroups(@Path("id") discordId: String = "session"): Response<List<Group>>
}