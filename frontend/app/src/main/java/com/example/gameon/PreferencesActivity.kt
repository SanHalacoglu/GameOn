package com.example.gameon

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.fetchGames
import com.example.gameon.api.methods.register
import com.example.gameon.classes.Game
import com.example.gameon.classes.Preferences
import com.example.gameon.composables.DropdownInput
import com.example.gameon.composables.SimpleHeader
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

class PreferencesActivity : ComponentActivity() {
    companion object {
        private const val TAG = "PreferencesActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val discordId = intent.getStringExtra("DiscordId")!!
        val gamesListState = mutableStateOf<List<Game>>(emptyList())

        val selectedLanguage = mutableStateOf<String?>(null)
        val selectedRegion = mutableStateOf<String?>(null)
        val selectedTimezone = mutableStateOf<String?>(null)
        val selectedSkillLevel = mutableStateOf<String?>(null)
        val selectedGame = mutableStateOf<Game?>(null)
        val canConfirm = mutableStateOf(false)

        lifecycleScope.launch {
            val gamesList = fetchGames(this@PreferencesActivity)
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

            Column (
                modifier = Modifier.fillMaxSize().background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                SimpleHeader("Welcome to GameOn", "Select Your Preferences")
                Preferences(
                    modifier = Modifier.weight(1F, true),
                    gamesList = gamesListState.value,
                    selectedLanguage = selectedLanguage,
                    selectedRegion = selectedRegion,
                    selectedTimezone = selectedTimezone,
                    selectedSkillLevel = selectedSkillLevel,
                    selectedGame = selectedGame
                )
                PreferenceFooter(canConfirm) {
                    lifecycleScope.launch {
                        register(this@PreferencesActivity, Preferences(
                            discord_id = discordId,
                            spoken_language = selectedLanguage.value ?: "",
                            time_zone = selectedTimezone.value ?: "",
                            skill_level = selectedSkillLevel.value  ?: "",
                            game_id = selectedGame.value?.game_id ?: 0
                        ))
                    }
                }
            }
        }
    }
}

@Composable
fun Preferences(
    modifier: Modifier,
    gamesList: List<Game>,
    selectedLanguage: MutableState<String?>,
    selectedRegion: MutableState<String?>,
    selectedTimezone: MutableState<String?>,
    selectedSkillLevel: MutableState<String?>,
    selectedGame: MutableState<Game?>
) {
    val regions = listOf("Africa", "America", "Antarctica", "Arctic", "Asia", "Atlantic",
        "Australia", "Europe", "Indian", "Pacific", "Etc")

    // Get list of all possible timezones
    // Then sort the list and remove any unnecessary entries
    var timezones = listOf("Please select a region.")
    if (selectedRegion.value != null)
        timezones = TimeZone.availableZoneIds.filter {
                timezone -> timezone.startsWith(selectedRegion.value!!)
        } .sorted()

    Column (
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DropdownInput(
            "Spoken Language", options = listOf("English", "Français"), selectedOption = selectedLanguage,
            displayText = { it }, outlined = true, modifier = Modifier.fillMaxWidth(0.9f)
        )
        DropdownInput(
            "Region", supportingText = "\"Etc\" shows UTC offset timezone options.",
            options = regions, selectedOption = selectedRegion,
            displayText = { it }, outlined = true, modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if(selectedRegion.value?.let { selectedTimezone.value?.startsWith(it) } == false)
                selectedTimezone.value = null
        }
        // Show Timezone once region inputted for ease of timezone selection
        if (selectedRegion.value != null)
            DropdownInput(
                "Timezone", supportingText = "Pick the most relevant timezone to you.",
                options = timezones, selectedOption = selectedTimezone,
                displayText = { it }, outlined = true, modifier = Modifier.fillMaxWidth(0.9f)
            )
        DropdownInput(
            "Skill Level", options=listOf("Competitive", "Casual"), selectedOption = selectedSkillLevel,
            displayText = { it }, outlined = true, modifier = Modifier.fillMaxWidth(0.9f)
        )
        DropdownInput(
            "Game", options = gamesList, selectedOption = selectedGame,
            displayText = { it.game_name }, outlined = true, modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}

@Composable
fun PreferenceFooter(canConfirm: MutableState<Boolean>, onConfirm: () -> Unit) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BlueDark)
            .defaultMinSize(minHeight = 100.dp)
            .padding(top = 30.dp, bottom = 70.dp)
    ) {
        Button(
            enabled = canConfirm.value,
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue,
                disabledContainerColor = Color(0x442C8DFF)
            ),
            modifier = Modifier
                .width(300.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "CONFIRM",
                fontFamily = FontFamily(Font(R.font.lato_black)),
                color = BlueDarker
            )
        }
    }
}