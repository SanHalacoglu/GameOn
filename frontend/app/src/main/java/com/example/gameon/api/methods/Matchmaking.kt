package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.MatchmakingApi
import com.example.gameon.api.interfaces.MatchmakingRequest
import com.example.gameon.api.interfaces.MatchmakingStatusResponse
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

suspend fun checkMatchmakingStatus(context: Context, discordId: String): String {
    val matchmakingApi = Api.init(context)
        .getInstance(false)
        .create(MatchmakingApi::class.java)

    val response: Response<MatchmakingStatusResponse> = matchmakingApi.checkMatchmakingStatus(discordId)

    return if (response.isSuccessful) {
        val statusMessage = response.body()?.message
        Log.d("API", "Response body: $statusMessage")
        when (response.body()?.message) {
            "Matchmaking in progress" -> "in_progress"
            "Matchmaking timed out" -> "timed_out"
            "Group found" -> "group_found"
            else -> "unknown"
        }
    } else {
        if (response.code() == 404) {
            Log.w("API", "Received 404: Matchmaking not in progress")
            "not_in_progress"
        } else {
            Log.e("API", "API call failed: ${response.code()}, ${response.errorBody()?.string()}")
            "error"
        }
    }
}