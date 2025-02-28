package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.UsersApi
import com.example.gameon.classes.Group

suspend fun getUserGroups(
    discordId: String? = null,
    context: Context,
): List<Group>  {
    val usersApi = Api.init(context).getInstance().create(UsersApi::class.java)

    val result = if (discordId != null)
        usersApi.getUserGroups(discordId)
    else usersApi.getUserGroups()

    Log.d("API", "Response Code: ${result.code()}")
    Log.d("API", "Response Body: ${result.body()}")

    return if (result.isSuccessful)
        result.body()!!
    else
        emptyList()
}