package com.example.gameon.composables

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.gameon.R
import com.example.gameon.ui.theme.BlueDark
import com.example.gameon.ui.theme.White

@Composable
fun TextInput(
    selectedOption: MutableState<String>,
    modifier: Modifier
) {
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    TextField(
        value = selectedOption.value,
        onValueChange = { selectedOption.value = it },
        label = { Text(
            "Reason",
            fontFamily=fontFamily
        ) },
        placeholder = { Text(
            "Why are you reporting this user",
            fontFamily=fontFamily
        ) },
        singleLine = false,
        textStyle = TextStyle(fontFamily = fontFamily),
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = White,
            unfocusedLabelColor = White,
            unfocusedContainerColor = BlueDark,
            unfocusedIndicatorColor = BlueDark,
            unfocusedPlaceholderColor = Color(0xFFAAAAAA),
            focusedTextColor = White,
            focusedLabelColor = White,
            focusedContainerColor = BlueDark,
            focusedIndicatorColor = White,
            focusedPlaceholderColor = Color(0xFFAAAAAA),
        ),
        modifier = modifier
    )
}