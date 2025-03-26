package com.example.gameon.api.methods

import android.content.Context
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.UsersApi
import com.example.gameon.classes.Group
import com.example.gameon.classes.User

suspend fun getUsers(context: Context): List<User> {
    val usersApi = Api.init(context).getInstance().create(UsersApi::class.java)

    val result = usersApi.getUsers()

    return if (result.isSuccessful)
        result.body()!!
    else
        emptyList()
}

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