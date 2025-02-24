package com.example.gameon.api.interfaces

import com.example.gameon.classes.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApi {

    @GET("/auth/login")
    suspend fun login(): Response<User>

    @GET("/auth/discord_callback")
    suspend fun discordCallback(@Query("code") code: String): Response<User>

}