package com.example.gameon.api.interfaces

import com.example.gameon.classes.Admin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminApi {

    @GET("/admins")
    suspend fun getAdmins(): Response<List<Admin>>

    @POST("/admins")
    suspend fun createAdmin(@Body admin: Admin): Response<Admin>

}