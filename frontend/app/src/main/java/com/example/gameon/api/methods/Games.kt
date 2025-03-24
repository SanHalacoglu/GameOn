package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.GamesApi
import retrofit2.Response
import com.example.gameon.classes.Game
import com.google.gson.Gson

suspend fun fetchGames(
    context: Context): List<Game> {

    val gamesApi = Api.init(context)
        .getInstance(false)
        .create(GamesApi::class.java)

    val result: Response<List<Game>> = gamesApi.getGames()

    return if (result.isSuccessful) {
        val games = result.body() ?: emptyList()
        Log.d("fetchGames", "Fetched games: $games")
        games
    } else {
        Log.e("fetchGames", "Failed to fetch games: ${result.errorBody()?.string()}")
        emptyList()
    }
}