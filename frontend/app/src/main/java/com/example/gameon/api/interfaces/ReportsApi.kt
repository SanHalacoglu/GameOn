package com.example.gameon.api.interfaces

import com.example.gameon.classes.Report
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportsApi {

    @GET("/reports")
    suspend fun getReports(@Query("unresolved") unresolved: Boolean): Response<List<Report>>

    @GET("/reports/{id}")
    suspend fun getReportById(@Path("id") id: Int): Response<Report>

    @POST("/reports")
    suspend fun createReport(@Body report: Report): Response<Any>

    @PUT("/reports/{id}/resolve")
    suspend fun resolveReport(@Path("id") id: Int, @Query("ban") ban: Boolean): Response<Report>

}