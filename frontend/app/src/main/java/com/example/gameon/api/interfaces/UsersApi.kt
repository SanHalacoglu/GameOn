package com.example.gameon.api.interfaces

import com.example.gameon.classes.Group
import com.example.gameon.classes.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UsersApi {

    @GET("/users")
    suspend fun getUsers(): Response<List<User>>

    @GET("/users/{id}/groups")
    suspend fun getUserGroups(@Path("id") discordId: String = "session"): Response<List<Group>>
}