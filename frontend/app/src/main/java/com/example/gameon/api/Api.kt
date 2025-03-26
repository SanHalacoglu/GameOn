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
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateAdapter())
        .create()

    fun getInstance(context: Context, followRedirects: Boolean = true): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .client(OkHttpClient
                .Builder()
                .cookieJar(PersistentCookieJar(context.applicationContext))
                .followRedirects(followRedirects)
                .build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}