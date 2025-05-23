package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.GamesApi
import retrofit2.Response
import com.example.gameon.classes.Game

suspend fun fetchGames(context: Context): List<Game> {
    val gamesApi = Api.getInstance(context, false)
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

suspend fun createGame(
    context: Context,
    name: String,
    description: String,
    group_size: Int
) {
    val gamesApi = Api.getInstance(context, false)
        .create(GamesApi::class.java)

    val result: Response<Game> = gamesApi.createGame(Game(game_name=name, description=description, group_size=group_size))

    if (result.isSuccessful) {
        val game = result.body()
        Log.d("createGame", "Created game: $game")
        Toast.makeText(context, "${game?.game_name} added!", Toast.LENGTH_SHORT).show()
    } else {
        Log.e("createGame", "Failed to create game: ${result.errorBody()?.string()}")
        Toast.makeText(context, "Error adding game!", Toast.LENGTH_SHORT).show()
    }
}