package com.example.gameon.api.interfaces

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class MatchmakingRequest(val preference_id: Int)

interface MatchmakingApi {
    @POST("/matchmaking/initiate")
    suspend fun initiateMatchmaking(@Body request: MatchmakingRequest): Response<Unit>
}