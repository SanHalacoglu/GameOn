package com.example.gameon.classes

import java.util.Date

data class User(
    val discord_id: String,
    val username: String,
    val email: String,
    val created_at: Date,
    val banned: Boolean,
    val preference_id: Int? = null,
    val preferences: Preferences? = null
)
