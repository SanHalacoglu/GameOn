package com.example.gameon.api

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieJar (private val context: Context) : CookieJar {
    private val prefs = context.getSharedPreferences("cookies_prefs", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieStrings = cookies.map { it.toString() }.toSet()
        prefs.edit().putStringSet(url.host(), cookieStrings).apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStrings = prefs.getStringSet(url.host(), emptySet()) ?: return emptyList()
        return cookieStrings.map { Cookie.parse(url, it)!! }
    }
}