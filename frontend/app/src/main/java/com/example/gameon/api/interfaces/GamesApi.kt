package com.example.gameon.api.interfaces

import com.example.gameon.classes.Game
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GamesApi {

    @GET("/games")
    suspend fun getGames(): Response<List<Game>>

    @POST("/games")
    suspend fun createGame(@Body game: Game): Response<Game>

}