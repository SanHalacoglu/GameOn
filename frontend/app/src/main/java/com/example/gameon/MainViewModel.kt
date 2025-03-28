package com.example.gameon

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameon.api.methods.checkMatchmakingStatus
import com.example.gameon.api.methods.initiateMatchmaking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val isMatchmakingActive = mutableStateOf(false)
    val matchmakingStatus = mutableStateOf<String?>(null)
    val showDialog = mutableStateOf(false)
    val dialogMessage = mutableStateOf("")

    fun startMatchmaking(context: Context, preferenceId: Int, discordId: String) {
        viewModelScope.launch {
            isMatchmakingActive.value = true
            val success = initiateMatchmaking(context, preferenceId)
            if (success) {
                matchmakingStatus.value = "in_progress"
                pollMatchmakingStatus(context, discordId)
            } else {
                isMatchmakingActive.value = false
                dialogMessage.value = "Failed to initiate matchmaking."
                showDialog.value = true
            }
        }
    }

    private fun pollMatchmakingStatus(context: Context, discordId: String) {
        viewModelScope.launch {
            while (isMatchmakingActive.value) {
                delay(10_000)
                val status = checkMatchmakingStatus(context, discordId)
                matchmakingStatus.value = status

                when (status) {
                    "in_progress" -> {}
                    "timed_out" -> {
                        isMatchmakingActive.value = false
                        dialogMessage.value = "Matchmaking timed out. Please try again."
                        showDialog.value = true
                    }
                    "group_found" -> {
                        isMatchmakingActive.value = false
                        dialogMessage.value = "You have been matched with a group!"
                        showDialog.value = true
                    }
                    "not_in_progress", "error" -> {
                        isMatchmakingActive.value = false
                    }
                }
            }
        }
    }
}
