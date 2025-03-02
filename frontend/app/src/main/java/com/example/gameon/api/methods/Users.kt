package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.UsersApi
import com.example.gameon.classes.Group
import com.example.gameon.classes.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

suspend fun getUserGroups(
    discordId: String? = null,
    context: Context,
): List<Group>  {
    val usersApi = Api.init(context).getInstance().create(UsersApi::class.java)

    val result = if (discordId != null)
        usersApi.getUserGroups(discordId)
    else usersApi.getUserGroups()

    return if (result.isSuccessful)
        result.body()!!
    else
        emptyList()

}