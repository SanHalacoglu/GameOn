package com.example.gameon.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.gameon.R
import com.example.gameon.ui.theme.BlueDarker
import com.example.gameon.ui.theme.White

@Composable
fun ReportButton(
    text: String,
    textColor: Color = White,
    containerColor: Color = BlueDarker,
    outlined: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier,
    onClick : () -> Unit = { }
) {

    OutlinedButton(
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = Color(containerColor.toArgb() - 0x88000000),
            contentColor = textColor,
        ),
        border = if (outlined) BorderStroke(1.dp, Color.White) else null,
        modifier = modifier
    ) {
        Text(
            text,
            fontFamily = FontFamily(Font(R.font.lato_bold)),
            color = if (enabled) textColor else Color(textColor.toArgb() - 0x88000000)
        )
    }
}