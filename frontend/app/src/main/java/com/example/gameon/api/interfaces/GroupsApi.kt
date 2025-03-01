package com.example.gameon.api.interfaces

import com.example.gameon.classes.GroupMember
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GroupsApi {

    @GET("groups/{id}/members")
    suspend fun getGroupMembers(@Path("id") groupId: Int): Response<List<GroupMember>>

}