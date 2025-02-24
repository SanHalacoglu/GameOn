package com.example.gameon.classes

import java.util.Date

data class GroupMember(
    val groupMemberId: Int,
    val discord_id: String? = null,
    val user: User? = null,
    val group_id: Int?,
    val group: Group?,
    val joined_at: Date
) {
    init {
        require( discord_id != null || user != null ) {
            "Either discord_id or user must not be null"
        }
        require( group_id != null || group != null ) {
            "Either group_id or group must not be null"
        }
    }
}
