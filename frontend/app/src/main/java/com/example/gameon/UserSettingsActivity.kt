package com.example.gameon

import android.content.Context
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.gameon.PreferenceComposables.Footer
import com.example.gameon.PreferenceComposables.Header
import com.example.gameon.PreferenceComposables.Preferences
import com.example.gameon.api.methods.SessionDetails
import com.example.gameon.api.methods.createUserPreferences
import com.example.gameon.api.methods.fetchGames
import com.example.gameon.api.methods.getUserPreferences
import com.example.gameon.api.methods.register
import com.example.gameon.api.methods.updatePreferences
import com.example.gameon.classes.Game
import com.example.gameon.classes.Preferences
import com.example.gameon.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

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
//        val preferenceId = user?.preferences?.preference_id ?: -1
        val preferenceId = user?.preferences?.preference_id?.toIntOrNull() ?: -1

        Log.d("UserSettings", "Extracted preference ID: $preferenceId")

        Log.d("UserSettings", "Preference id: $preferenceId")

        val selectedLanguage = mutableStateOf("")
        val selectedRegion = mutableStateOf("")
        val selectedTimezone = mutableStateOf("")
        val selectedSkillLevel = mutableStateOf("")
        val selectedGameName = mutableStateOf("")

        lifecycleScope.launch {
            val preferences = getUserPreferences(preferenceId, this@UserSettingsActivity)

            if (preferences != null) {
                Log.d("UserSettings", "Fetched Preferences: $preferences")

                // Populate fields with existing preferences
                selectedLanguage.value = preferences.spoken_language ?: ""
                selectedTimezone.value = preferences.time_zone ?: ""
                selectedSkillLevel.value = preferences.skill_level ?: ""
                selectedGameName.value = preferences.game?.game_name ?: ""

                // Extract the correct region from the timezone (e.g., "America/New_York" -> "America")
                selectedRegion.value = preferences.time_zone?.split("/")?.firstOrNull() ?: ""

            } else {
                Log.e("UserSettings", "Failed to fetch preferences")
            }
        }

        lifecycleScope.launch {
            val gamesList = fetchGames(this@UserSettingsActivity) // Call API
            gamesListState.value = gamesList // Update state
            Log.d(TAG, "Received games: $gamesList")
        }

        Log.d(TAG, "Discord ID: $discordId")

        setContent {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SettingsHeader()
                Preferences(
                    modifier = Modifier.weight(1F, true),
                    gamesList = gamesListState.value,
                    selectedLanguage = selectedLanguage,
                    selectedRegion = selectedRegion,
                    selectedTimezone = selectedTimezone,
                    selectedSkillLevel = selectedSkillLevel,
                    selectedGameName = selectedGameName
                )
                UserSettingsComposables.Footer(
                    onConfirm = {
                        val selectedGameObject = gamesListState.value.find { it.game_name == selectedGameName.value }
                        val gameId = selectedGameObject?.game_id ?: 0 // Default to 0 if not found

                        val preferences = Preferences(
                            preference_id = preferenceId.toString(), // Ensure this is passed
                            discord_id = discordId,
                            spoken_language = selectedLanguage.value,
                            time_zone = selectedTimezone.value,
                            skill_level = selectedSkillLevel.value,
                            game_id = gameId
                        )

                        Log.d("PreferencesActivity", "Creating preferences: $preferences")
                    },
                    context = this@UserSettingsActivity,
                    preferenceId = preferenceId, // Pass it here
                    preferences = Preferences(
                        preference_id = preferenceId.toString(),
                        discord_id = discordId,
                        spoken_language = selectedLanguage.value,
                        time_zone = selectedTimezone.value,
                        skill_level = selectedSkillLevel.value,
                        game_id = gamesListState.value.find { it.game_name == selectedGameName.value }?.game_id ?: 0
                    )
                )
            }
        }
    }
}

@Composable
fun SettingsHeader() {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BlueDark)
            .height(150.dp)
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Text(
                text = "User Settings",
                color = BlueLight,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 36.sp,
                    shadow = Shadow(
                        color = Blue,
                        blurRadius = 10F
                    ),
                )
            )
            //Text stroke
            Text(
                text = "User Settings",
                color = Blue,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 36.sp,
                    drawStyle = Stroke(0.5F)
                )
            )
        }
    }
}

object UserSettingsComposables {

    private val REGIONS = listOf(
        "Africa",
        "America",
        "Antarctica", //feature, not bug
        "Arctic",
        "Asia",
        "Atlantic",
        "Australia",
        "Europe",
        "Indian",
        "Pacific",
        "Etc"
    )

    private val canConfirm = mutableStateOf(false)

    @Composable
    fun Preferences(
        modifier: Modifier,
        gamesList: List<Game>,
        selectedLanguage: MutableState<String>,
        selectedRegion: MutableState<String>,
        selectedTimezone: MutableState<String>,
        selectedSkillLevel: MutableState<String>,
        selectedGameName: MutableState<String>
    ) {

        // Get list of all possible timezones
        // Then sort the list and remove any unnecessary entries
        var timezones by remember { mutableStateOf(listOf("Please select a region.")) }

        LaunchedEffect(selectedRegion.value) {
            if (selectedRegion.value.isNotEmpty()) {
                timezones = TimeZone.availableZoneIds
                    .filter { it.startsWith(selectedRegion.value) }
                    .sorted()
            }
        }
        Column (
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PreferenceInput(
                "Spoken Language",
                options=listOf("English", "Français"),
                selectedOption = selectedLanguage
            )
            PreferenceInput(
                "Region",
                "\"Etc\" shows UTC offset timezone options.",
                options = REGIONS,
                selectedOption = selectedRegion
            ) {
                selectedTimezone.value = ""
            }
            // Show Timezone once region inputted
            // For ease of timezone selection
            if (selectedRegion.value.isNotEmpty())
                PreferenceInput(
                    "Timezone",
                    "Pick the most relevant timezone to you.",
                    options = timezones,
                    selectedOption = selectedTimezone
                )
            PreferenceInput(
                "Skill Level",
                options=listOf("Competitive", "Casual"),
                selectedOption = selectedSkillLevel
            )
            PreferenceInput("Game",
                options = gamesList.map { it.game_name },
                selectedOption = selectedGameName
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PreferenceInput(
        label: String,
        supportingText: String = "",
        options: List<String>,
        selectedOption: MutableState<String>,
        onSelect: () -> Unit = {}
    ) {
        var expanded by remember { mutableStateOf(false) }
        val fontFamily = FontFamily(Font(R.font.lato_regular))

        Column {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedOption.value,
                    onValueChange = { },
                    label = {
                        Text(
                            label.uppercase(),
                            fontFamily = fontFamily
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = White,
                        unfocusedLabelColor = White,
                        unfocusedTextColor = White,
                        unfocusedTrailingIconColor = White,
                        focusedBorderColor = BlueLight,
                        focusedLabelColor = BlueLight,
                        focusedTextColor = BlueLight,
                        focusedTrailingIconColor = BlueLight,
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    textStyle = TextStyle(fontFamily = fontFamily),
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier
                        .background(BlueDark)
                        .padding(horizontal = 2.dp)
                        .heightIn(max = 250.dp)
                ) {
                    options.forEach { option ->
                        val textColor: Color
                        val bgColor: Color
                        if (selectedOption.value == option) {
                            textColor = BlueLight
                            bgColor = BlueDarker
                        } else {
                            textColor = White
                            bgColor = BlueDark
                        }
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOption.value = option
                                onSelect()
                                expanded = false
                            },
                            colors = MenuItemColors(
                                textColor = textColor,
                                leadingIconColor = Color.Transparent,
                                trailingIconColor = Color.Transparent,
                                disabledTextColor = Color.Transparent,
                                disabledLeadingIconColor = Color.Transparent,
                                disabledTrailingIconColor = Color.Transparent
                            ),
                            modifier = Modifier.background(bgColor)
                        )
                    }
                }
            }
            if (supportingText.isNotEmpty())
                Text(
                    supportingText,
                    fontFamily=fontFamily,
                    fontSize = 12.sp,
                    color = Color(0xBBFFFFFF),
                    modifier = Modifier.padding(horizontal=16.dp)
                )
        }
    }
    @Composable
    fun Footer(
        onConfirm: () -> Unit,
        context: Context,
        preferenceId: Int, // Make sure this is passed
        preferences: Preferences
    ) {
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = BlueDark)
                .defaultMinSize(minHeight = 100.dp)
                .padding(top = 30.dp, bottom = 70.dp)
        ) {
            Button(
                onClick = {
                    onConfirm()
                    coroutineScope.launch {
                        if (preferenceId > 0) { // Ensure a valid ID is being sent
                            val success = updatePreferences(context, preferenceId, preferences)
                            if (success) {
                                Log.d("Footer", "Preferences updated successfully!")
                                (context as? ComponentActivity)?.finish()
                            } else {
                                Log.e("Footer", "Failed to update preferences")
                            }
                        } else {
                            Log.e("Footer", "Invalid preferenceId: $preferenceId")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue,
                    disabledContainerColor = Color(0x442C8DFF) // Keep it enabled
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
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreferencesPreview() {
//    val selectedLanguage = remember { mutableStateOf("English") }
//    val selectedRegion = remember { mutableStateOf("Arctic") }
//    val selectedTimezone = remember { mutableStateOf("Arctic/Longyearbyen") }
//    val selectedSkillLevel = remember { mutableStateOf("Competitive") }
//    val selectedGameName = remember { mutableStateOf("The Sims") }
//    val sampleGames = listOf(
//        Game(game_id = 1, game_name = "The Sims",""),
//        Game(game_id = 2, game_name = "Minecraft",""),
//        Game(game_id = 3, game_name = "Valorant","")
//    ) // Mock list of games for preview
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = BlueDarker),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        SettingsHeader() // Include the header in the preview
//
//        Preferences(
//            modifier = Modifier.weight(1F, true),
//            gamesList = sampleGames, // Pass mock data
//            selectedLanguage = selectedLanguage,
//            selectedRegion = selectedRegion,
//            selectedTimezone = selectedTimezone,
//            selectedSkillLevel = selectedSkillLevel,
//            selectedGameName = selectedGameName
//        )
//
//        Footer(
//            onConfirm = {
//                Log.d("Preview", "Confirm button clicked")
//            },
//            context = LocalContext.current, // Pass null for preview
//            preferences = Preferences(
//                discord_id = "1234567890",
//                spoken_language = selectedLanguage.value,
//                time_zone = selectedTimezone.value,
//                skill_level = selectedSkillLevel.value,
//                game_id = sampleGames.find { it.game_name == selectedGameName.value }?.game_id ?: 0
//            )
//        )
//    }
//}
