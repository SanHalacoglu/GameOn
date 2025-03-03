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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.gameon.api.methods.fetchGames
import com.example.gameon.api.methods.register
import com.example.gameon.classes.Game
import com.example.gameon.classes.Preferences
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

        val selectedLanguage = mutableStateOf("")
        val selectedRegion = mutableStateOf("")
        val selectedTimezone = mutableStateOf("")
        val selectedSkillLevel = mutableStateOf("")
        val selectedGameName = mutableStateOf("")

        lifecycleScope.launch {
            val gamesList = fetchGames(this@PreferencesActivity)
            gamesListState.value = gamesList
            Log.d(TAG, "Received games: $gamesList")
        }

        Log.d(TAG, "Discord ID: $discordId")

        setContent {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Header()
                Preferences(
                    modifier = Modifier.weight(1F, true),
                    gamesList = gamesListState.value,
                    selectedLanguage = selectedLanguage,
                    selectedRegion = selectedRegion,
                    selectedTimezone = selectedTimezone,
                    selectedSkillLevel = selectedSkillLevel,
                    selectedGameName = selectedGameName
                )
                Footer(onConfirm = {
                    val selectedGameObject = gamesListState.value.find { it.game_name == selectedGameName.value }
                    val gameId = selectedGameObject?.game_id ?: 0

                    val preferences = Preferences(
                        discord_id = discordId,
                        spoken_language = selectedLanguage.value,
                        time_zone = selectedTimezone.value,
                        skill_level = selectedSkillLevel.value,
                        game_id = gameId
                    )

                    lifecycleScope.launch {
                        register(this@PreferencesActivity, preferences)
                    }
                })
            }
        }
    }
}

object PreferenceComposables {

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
        // Enables Footer's confirm button when all preferences selected
        canConfirm.value = (selectedLanguage.value.isNotEmpty()) &&
                (selectedRegion.value.isNotEmpty()) &&
                (selectedTimezone.value.isNotEmpty()) &&
                (selectedSkillLevel.value.isNotEmpty()) &&
                (selectedGameName.value.isNotEmpty())

        // Get list of all possible timezones
        // Then sort the list and remove any unnecessary entries
        var timezones = listOf("Please select a region.")
        if (selectedRegion.value.isNotEmpty())
            timezones = TimeZone.availableZoneIds.filter {
                    timezone -> timezone.startsWith(selectedRegion.value)
            } .sorted()

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
                if(!selectedTimezone.value.startsWith(selectedRegion.value))
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
    fun Header() {
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
                //Text fill and shadow
                Text(
                    text = "Welcome to GameOn",
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
                    text = "Welcome to GameOn",
                    color = Blue,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = 36.sp,
                        drawStyle = Stroke(0.5F)
                    )
                )
            }
            Text(
                text = "Select Your Preferences",
                color = Blue,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 24.sp,
                )
            )
        }
    }

    @Composable
    fun Footer(
        onConfirm: () -> Unit
    ) {
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
}
