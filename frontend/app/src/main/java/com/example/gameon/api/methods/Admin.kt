package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.AdminApi
import com.example.gameon.classes.Admin
import retrofit2.Response

suspend fun getAdmins(context: Context): List<Admin> {
    val adminApi = Api.getInstance(context, false)
        .create(AdminApi::class.java)

    val result: Response<List<Admin>> = adminApi.getAdmins()

    return if (result.isSuccessful) {
        val admins = result.body() ?: emptyList()
        Log.d("getAdmins", "Fetched admins: $admins")
        admins
    } else {
        Log.e("getAdmins", "Failed to fetch admins: ${result.errorBody()?.string()}")
        emptyList()
    }
}

suspend fun createAdmin(
    context: Context,
    discordId: String
): Boolean {
    val adminApi = Api.getInstance(context, false)
        .create(AdminApi::class.java)

    val result: Response<Admin> = adminApi.createAdmin(Admin(discord_id = discordId))

    return if (result.isSuccessful) {
        val admin = result.body()
        Log.d("createAdmins", "New admin created: $admin")
        true
    } else {
        Log.e("createAdmins", "Failed to create admin: ${result.errorBody()?.string()}")
        false
    }
}