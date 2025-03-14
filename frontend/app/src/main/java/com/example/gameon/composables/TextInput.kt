package com.example.gameon.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.BlueDark
import com.example.gameon.ui.theme.White
import com.example.gameon.ui.theme.Red
import com.example.gameon.ui.theme.RedDark
import com.example.gameon.ui.theme.RedLight

@Composable
fun TextInput(
    input: MutableState<String>,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    isError: Boolean = false
) {
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TextField(
            value = input.value,
            onValueChange = { input.value = it },
            label = { Text(
                "Reason",
                fontFamily=fontFamily
            ) },
            placeholder = { Text(
                "Why are you reporting this user?",
                fontFamily=fontFamily
            ) },
            singleLine = false,
            textStyle = TextStyle(fontFamily = fontFamily),
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = White,
                unfocusedLabelColor = White,
                unfocusedContainerColor = BlueDark,
                unfocusedIndicatorColor = BlueDark,
                unfocusedPlaceholderColor = Color(0xAAFFFFFF),
                focusedTextColor = White,
                focusedLabelColor = White,
                focusedContainerColor = BlueDark,
                focusedIndicatorColor = White,
                focusedPlaceholderColor = Color(0xAAFFFFFF),
                errorTextColor = White,
                errorLabelColor = RedLight,
                errorContainerColor = RedDark,
                errorIndicatorColor = Red,
                errorPlaceholderColor = Color(0xAAFFFFFF)
            ),
            isError = isError,
            modifier = Modifier.fillMaxWidth().weight(1F)
        )
        if (isError && errorText != null)
            Text(
                errorText,
                fontFamily=fontFamily,
                fontSize = 10.sp,
                color = White,
                modifier = Modifier.fillMaxWidth()
            )
    }
}