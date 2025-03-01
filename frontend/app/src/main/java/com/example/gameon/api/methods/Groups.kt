package com.example.gameon.api.methods

import android.content.Context
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.GroupsApi
import com.example.gameon.classes.GroupMember

suspend fun getGroupMembers(
    groupId: Int,
    context: Context,
): List<GroupMember> {
    val groupsApi = Api.init(context).getInstance().create(GroupsApi::class.java)

    val result = groupsApi.getGroupMembers(groupId)

    return if (result.isSuccessful)
        result.body()!!
    else
        emptyList()
}