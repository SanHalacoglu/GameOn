package com.example.gameon.api.interfaces

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class MatchmakingRequest(val preference_id: Int)
data class MatchmakingStatusResponse(val message: String, val timestamp: String?)

interface MatchmakingApi {
    @POST("/matchmaking/initiate")
    suspend fun initiateMatchmaking(@Body request: MatchmakingRequest): Response<Unit>

    @GET("/matchmaking/status/{discord_id}")
    suspend fun checkMatchmakingStatus(@Path("discord_id") discordId: String): Response<MatchmakingStatusResponse>
}
