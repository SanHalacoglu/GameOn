package com.example.gameon.api

import android.content.Context
import com.example.gameon.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private lateinit var appContext: Context

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
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}