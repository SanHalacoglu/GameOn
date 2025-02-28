package com.example.gameon.api

import android.content.Context
import com.example.gameon.BuildConfig
import com.example.gameon.classes.DateAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

object Api {
    private lateinit var appContext: Context

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateAdapter())
        .create()

    fun init(context: Context): Api {
        appContext = context.applicationContext
        return this@Api
    }

    fun getInstance(followRedirects: Boolean = true): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .client(OkHttpClient
                .Builder()
                .cookieJar(PersistentCookieJar(appContext))
                .followRedirects(followRedirects)
                .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}