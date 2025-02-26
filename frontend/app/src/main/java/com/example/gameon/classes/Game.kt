package com.example.gameon.classes

data class Game(
    val game_id: Int,
    val game_name: String,
    val description: String,
    val groups: List<Group>? = null
)