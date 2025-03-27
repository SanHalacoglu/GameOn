package com.example.gameon.api.interfaces

import com.example.gameon.classes.Group
import com.example.gameon.classes.GroupMember
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupsApi {

    @PUT("groups/{id}")
    suspend fun updateGroup(@Path("id") groupId: Int, @Body group: Group): Response<Group>

    @GET("groups/{id}/members")
    suspend fun getGroupMembers(@Path("id") groupId: Int): Response<List<GroupMember>>

    @GET("groups/{id}/url")
    suspend fun getGroupUrl(@Path("id") groupId: Int): Response<GroupUrlResponse>

    data class GroupUrlResponse(
        @SerializedName("groupurl")
        val groupUrl: String
    )
}