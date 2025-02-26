package com.example.gameon.classes

data class Admin(
    val admin_id: Int,
    val discord_id: String? = null,
    val user: User? = null,
    val permissions: String,
) {
    init {
        require( discord_id != null || user != null ) {
            "Either discord_id or user must not be null"
        }
    }
}
