package com.example.gameon.api.methods

import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.GroupsApi
import com.example.gameon.classes.GroupMember

suspend fun getGroupMembers(
    groupId: Int,
    context: Context,
): List<GroupMember> {
    return try {
        val groupsApi = Api.getInstance(context).create(GroupsApi::class.java)
        val result = groupsApi.getGroupMembers(groupId)

        if (result.isSuccessful) {
            result.body() ?: emptyList()  // Return empty list if response body is null
        } else {
            Log.e("API_ERROR", "Failed to fetch group members: ${result.code()}")
            emptyList()  // Return empty list instead of throwing an error
        }
    } catch (e: Exception) {
        Log.e("NETWORK_ERROR", "Error fetching group members: ${e.message}")
        emptyList()  // Handle network failures gracefully
    }
}

suspend fun fetchGroupUrl(groupId: Int, context: Context): String? {
    val groupsApi = Api.getInstance(context).create(GroupsApi::class.java)

    val result = groupsApi.getGroupUrl(groupId)

    return if (result.isSuccessful)
        result.body()?.groupUrl
    else
        null
}