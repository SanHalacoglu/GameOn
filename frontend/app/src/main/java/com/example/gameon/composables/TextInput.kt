package com.example.gameon.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.BlueDark
import com.example.gameon.ui.theme.White
import com.example.gameon.ui.theme.Red
import com.example.gameon.ui.theme.RedDark
import com.example.gameon.ui.theme.RedLight

@Composable
fun TextInput(
    label: String,
    input: MutableState<String>,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    placeholder: String? = null,
    singleLine: Boolean = false,
    errorText: String? = null,
    isError: Boolean = false,
    containerColor: Color = BlueDark
) {
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        TextField(
            value = input.value,
            onValueChange = { input.value = it },
            label = { Text(label, fontFamily=fontFamily) },
            placeholder = { placeholder?.let { Text(it, fontFamily=fontFamily) } },
            singleLine = singleLine,
            textStyle = TextStyle(fontFamily = fontFamily, fontSize = fontSize),
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = White,
                unfocusedLabelColor = White,
                unfocusedContainerColor = containerColor,
                unfocusedIndicatorColor = containerColor,
                unfocusedPlaceholderColor = Color(0xAAFFFFFF),
                focusedTextColor = White,
                focusedLabelColor = White,
                focusedContainerColor = containerColor,
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