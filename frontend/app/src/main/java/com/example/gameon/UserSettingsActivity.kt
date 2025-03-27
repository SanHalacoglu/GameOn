package com.example.gameon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.fetchGames
import com.example.gameon.api.methods.getPreferencesByUserId
import com.example.gameon.api.methods.updatePreferences
import com.example.gameon.classes.Game
import com.example.gameon.classes.Preferences
import com.example.gameon.composables.SimpleHeader
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch

class UserSettingsActivity : ComponentActivity() {
    companion object {
        private const val TAG = "UserSettingsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val discordId = SessionDetails(this).getUser()?.discord_id ?: "-1"
        Log.d("UserSettings", "Discord ID: $discordId")
        
        val gamesList = mutableStateOf<List<Game>>(emptyList())
        val preferenceId = mutableStateOf(-1)

        val selectedLanguage = mutableStateOf<String?>(null)
        val selectedRegion = mutableStateOf<String?>(null)
        val selectedTimezone = mutableStateOf<String?>(null)
        val selectedSkillLevel = mutableStateOf<String?>(null)
        val selectedGame = mutableStateOf<Game?>(null)
        val canConfirm = mutableStateOf(false)

        lifecycleScope.launch {
            val preferences = getPreferencesByUserId(this@UserSettingsActivity, discordId)
            if (preferences != null) {
                preferenceId.value = preferences.preference_id ?: -1

                selectedLanguage.value = preferences.spoken_language
                selectedTimezone.value = preferences.time_zone
                selectedSkillLevel.value = preferences.skill_level.capitalize(Locale.current)
                selectedGame.value = preferences.game
                selectedRegion.value = preferences.time_zone.split("/").firstOrNull()
            }
        }

        lifecycleScope.launch { gamesList.value = fetchGames(this@UserSettingsActivity) }

        setContent {
            canConfirm.value = selectedLanguage.value != null && selectedRegion.value != null &&
                    selectedTimezone.value != null && selectedSkillLevel.value != null &&
                    selectedGame.value != null

            Column(
                modifier = Modifier.fillMaxSize().background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimpleHeader("User Settings")
                Preferences(
                    Modifier.weight(1F, true), gamesList.value, selectedLanguage, 
                    selectedRegion, selectedTimezone, selectedSkillLevel, selectedGame
                )
                PreferenceFooter(canConfirm){ lifecycleScope.launch {
                    val preferences = Preferences(
                        preferenceId.value, selectedLanguage.value ?: "",
                        selectedTimezone.value ?: "", selectedSkillLevel.value ?: "",
                        discordId, game_id = selectedGame.value?.game_id ?: 0
                    )
                    Log.d(TAG, "Saving preferences: $preferences")
                    updatePreferences(this@UserSettingsActivity, preferenceId.value, preferences)
                } }
            }
        }
    }
}
