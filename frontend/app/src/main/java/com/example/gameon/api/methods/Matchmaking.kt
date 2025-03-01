package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.MatchmakingApi
import com.example.gameon.api.interfaces.MatchmakingRequest
import retrofit2.Response

suspend fun initiateMatchmaking(
    context: Context,
    preferenceId: Int
): Boolean {
    val matchmakingApi = Api.init(context)
        .getInstance(false)
        .create(MatchmakingApi::class.java)

    val request = MatchmakingRequest(preference_id = preferenceId)
    val result: Response<Unit> = matchmakingApi.initiateMatchmaking(request)

    return if (result.isSuccessful) {
        Log.d("Matchmaking", "Matchmaking initiated successfully")
        true
    } else {
        Log.e("Matchmaking", "Failed to initiate matchmaking: ${result.errorBody()?.string()}")
        false
    }
}