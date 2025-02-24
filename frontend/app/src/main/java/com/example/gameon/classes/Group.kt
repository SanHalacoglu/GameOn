package com.example.gameon.classes

import java.util.Date

data class Group(
    val group_id: Int,
    val group_name: String,
    val created_at: Date,
    val max_players: Int,
    val game_id: Int? = null,
    val game: Game? = null,
    val members: List<GroupMember>? = null
) {
    init {
        require(game_id != null || game != null) {
            "Either game_id or game must be non-null"
        }
    }
}