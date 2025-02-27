package com.example.gameon.api.interfaces

import com.example.gameon.classes.Report
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReportsApi {

    @GET("/reports")
    suspend fun getReports(): Response<List<Report>>

    @POST("/reports")
    suspend fun createReport(@Body report: Report): Response<Any>

}