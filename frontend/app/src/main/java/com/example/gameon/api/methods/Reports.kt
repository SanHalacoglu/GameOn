package com.example.gameon.api.methods

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.gameon.api.Api
import com.example.gameon.api.interfaces.ReportsApi
import com.example.gameon.classes.Report

suspend fun submitReport(
    report: Report,
    context: Context,
) {
    val reportsApi = Api.init(context).getInstance().create(ReportsApi::class.java)

    val result = reportsApi.createReport(report)

    if (result.isSuccessful) {
        (context as? Activity)?.finish()
    } else {
        Log.d("Reports", "Something went wrong!")
    }
}

suspend fun getReports(
    context: Context,
    onSuccess: (reports: List<Report>) -> Unit,
    onFailure: () -> Unit
) {
    val reportsApi = Api.init(context).getInstance().create(ReportsApi::class.java)

    val result = reportsApi.getReports()

    if (result.isSuccessful) {
        onSuccess(result.body()!!)
    } else {
        Log.d("Reports", "Something went wrong!")
    }
}