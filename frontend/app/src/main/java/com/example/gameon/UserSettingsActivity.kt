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

        val user = SessionDetails(this).getUser()
        Log.d("UserSettings", "User: $user")
        val discordId =  user?.discord_id ?: "-1"
        val gamesListState = mutableStateOf<List<Game>>(emptyList())
        val preferenceIDState = mutableStateOf(-1)

        val selectedLanguage = mutableStateOf<String?>(null)
        val selectedRegion = mutableStateOf<String?>(null)
        val selectedTimezone = mutableStateOf<String?>(null)
        val selectedSkillLevel = mutableStateOf<String?>(null)
        val selectedGame = mutableStateOf<Game?>(null)
        val canConfirm = mutableStateOf(false)

        lifecycleScope.launch {
            val preferences = getPreferencesByUserId(this@UserSettingsActivity, discordId)
            if (preferences != null)
                preferenceIDState.value = preferences.preference_id ?: -1

            if (preferences != null) {
                Log.d("UserSettings", "Fetched Preferences: $preferences")

                selectedLanguage.value = preferences.spoken_language
                selectedTimezone.value = preferences.time_zone
                selectedSkillLevel.value = preferences.skill_level.capitalize(Locale.current)
                selectedGame.value = preferences.game
                selectedRegion.value = preferences.time_zone.split("/").firstOrNull()
            } else Log.e("UserSettings", "Failed to fetch preferences")
        }

        lifecycleScope.launch {
            val gamesList = fetchGames(this@UserSettingsActivity)
            gamesListState.value = gamesList
            Log.d(TAG, "Received games: $gamesList")
        }

        Log.d(TAG, "Discord ID: $discordId")

        setContent {
            canConfirm.value = selectedLanguage.value != null &&
                    selectedRegion.value != null &&
                    selectedTimezone.value != null &&
                    selectedSkillLevel.value != null &&
                    selectedGame.value != null

            Column(
                modifier = Modifier.fillMaxSize().background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SimpleHeader("User Settings")
                Preferences(
                    modifier = Modifier.weight(1F, true),
                    gamesList = gamesListState.value,
                    selectedLanguage = selectedLanguage,
                    selectedRegion = selectedRegion,
                    selectedTimezone = selectedTimezone,
                    selectedSkillLevel = selectedSkillLevel,
                    selectedGame = selectedGame
                )
               PreferenceFooter(canConfirm){
                   lifecycleScope.launch {
                       val preferences = Preferences(
                           preference_id = preferenceIDState.value,
                           discord_id = discordId,
                           spoken_language = selectedLanguage.value ?: "",
                           time_zone = selectedTimezone.value ?: "",
                           skill_level = selectedSkillLevel.value ?: "",
                           game_id = selectedGame.value?.game_id ?: 0
                       )
                       Log.d(TAG, "Saving preferences: $preferences")
                       updatePreferences(this@UserSettingsActivity, preferenceIDState.value, preferences)
                   }
                }
            }
        }
    }
}
