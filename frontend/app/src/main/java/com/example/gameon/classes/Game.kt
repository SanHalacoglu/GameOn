package com.example.gameon.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Game(
    val game_id: Int,
    val game_name: String,
    val description: String,
    val groups: List<Group>? = null
): Parcelable