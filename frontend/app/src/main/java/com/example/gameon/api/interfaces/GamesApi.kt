package com.example.gameon.api.interfaces

import com.example.gameon.classes.Game
import retrofit2.Response
import retrofit2.http.GET

interface GamesApi {

    @GET("/games")
    suspend fun getGames(): Response<List<Game>>

}