package com.example.gameon.api.methods

import android.content.Context
import android.content.SharedPreferences
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

    // Save user's admin ID
    // Default -1 if user is not an admin
    fun saveAdminId(adminId: Int?) {
        prefs.edit().putInt("admin_id", adminId ?: -1).apply()
    }

    // Retrieve user's admin ID
    fun getAdminId(): Int {
        return prefs.getInt("admin_id", -1)
    }

    // Clear all stored user data (used for logout)
    fun clearUser() {
        prefs.edit().remove("user_data").remove("admin_id").apply()
    }
}