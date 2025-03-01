package com.example.gameon.api.methods

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.gameon.ListReportsActivity
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
    unresolved: Boolean = false,
    context: Context,
): List<Report> {
    val reportsApi = Api.init(context).getInstance().create(ReportsApi::class.java)

    val result = reportsApi.getReports(unresolved)

    return if (result.isSuccessful) {
        result.body()!!
    } else {
        Log.d("Reports", "Something went wrong!")
        emptyList()
    }
}

suspend fun getReportById(
    reportId: Int,
    context: Context
): Report? {
    val reportsApi = Api.init(context).getInstance().create(ReportsApi::class.java)

    val result = reportsApi.getReportById(reportId)

    return if (result.isSuccessful) {
        result.body()!!
    } else {
        Log.d("Reports", "Something went wrong!")
        null
    }
}

suspend fun resolveReport(
    reportId: Int,
    ban: Boolean,
    context: Context
) {
    val reportsApi = Api.init(context).getInstance().create(ReportsApi::class.java)

    val result = reportsApi.resolveReport(reportId, ban)

    val intent = if (result.isSuccessful) {
        Intent(context, ListReportsActivity::class.java)
    } else {
        Log.e("Reports", "Failed to resolve report: ${result.errorBody()?.string()}")
        return
    }

    intent.let {
        context.startActivity(it)
        (context as? Activity)?.finish()
    }
}