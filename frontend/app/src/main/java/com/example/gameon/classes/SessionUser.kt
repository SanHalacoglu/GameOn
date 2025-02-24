package com.example.gameon.classes

data class SessionUser (
    val discord_id: String,
    val discord_access_token: String,
    val discord_refresh_token: String,
    val discord_username: String? = null,
    val discord_email: String? = null,
    val temp_session: Boolean
)