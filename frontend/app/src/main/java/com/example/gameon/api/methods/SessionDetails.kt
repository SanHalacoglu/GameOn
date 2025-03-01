package com.example.gameon.api.methods

import android.content.Context
import android.content.SharedPreferences
import com.example.gameon.classes.Preferences
import com.example.gameon.classes.User
import com.google.gson.Gson

class SessionDetails(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Save the entire User object
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString("user_data", userJson).apply()
    }

    // Retrieve the entire User object
    fun getUser(): User? {
        val userJson = prefs.getString("user_data", null) ?: return null
        return gson.fromJson(userJson, User::class.java)
    }

    // Clear all stored user data (used for logout)
    fun clearUser() {
        prefs.edit().remove("user_data").apply()
    }

    fun getDiscordId(): String? = getUser()?.discord_id

    fun getUsername(): String? = getUser()?.username

    fun getEmail(): String? = getUser()?.email

    fun isBanned(): Boolean = getUser()?.banned ?: false

    fun getPreferenceId(): Int? = getUser()?.preference_id

    fun getPreferences(): Preferences? = getUser()?.preferences
}