package com.example.gameon

import android.os.Bundle
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
import com.example.gameon.PreferenceComposables.Footer
import com.example.gameon.PreferenceComposables.Header
import com.example.gameon.PreferenceComposables.Preferences
import com.example.gameon.ui.theme.*
import kotlinx.datetime.TimeZone
import java.time.ZoneId

class PreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Header()
                Preferences(Modifier.weight(1F, true))
                Footer()
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

    private val selectedLanguage = mutableStateOf("")
    private val selectedRegion = mutableStateOf("")
    private val selectedTimezone = mutableStateOf("")
    private val selectedSkillLevel = mutableStateOf("")
    private val selectedGame = mutableStateOf("")

    private val canConfirm = mutableStateOf(false)

    @Composable
    fun Preferences(modifier: Modifier) {
        //Enables Footer's confirm button when all preferences selected
        canConfirm.value = (selectedLanguage.value != "") &&
                (selectedRegion.value != "") &&
                (selectedTimezone.value != "") &&
                (selectedSkillLevel.value != "") &&
                (selectedGame.value != "")

        // Get list of all possible timezones
        // Then sort the list and remove any unnecessary entries
        var timezones = listOf("Please select a region.")
        if (selectedRegion.value != "")
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
            if (selectedRegion.value != "")
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
                options = listOf("League of Legends", "Dota 2", "Minecraft"),
                selectedOption = selectedGame
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
        val blank = Color(0)

        Column() {
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
                    modifier = Modifier.menuAnchor()
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
                                leadingIconColor = blank,
                                trailingIconColor = blank,
                                disabledTextColor = blank,
                                disabledLeadingIconColor = blank,
                                disabledTrailingIconColor = blank
                            ),
                            modifier = Modifier.background(bgColor)
                        )
                    }
                }
            }
            if (supportingText != "")
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
    fun Footer() {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = BlueDark)
                .defaultMinSize(minHeight = 100.dp)
                .padding(top = 30.dp, bottom = 70.dp)
        ) {
            Button(
                enabled = canConfirm.value,
                onClick = { },
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

@Preview(showBackground = true)
@Composable
fun PreferencesPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlueDarker),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Header()
        Preferences(Modifier.weight(1F, true))
        Footer()
    }
}
