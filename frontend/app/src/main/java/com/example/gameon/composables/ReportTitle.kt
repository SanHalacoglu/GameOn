package com.example.gameon.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.Blue

@Composable
fun ReportTitle(
    text: String,
    modifier: Modifier
){
    Text(
        text,
        color = Blue,
        fontFamily = FontFamily(Font(R.font.barlowcondensed_bold)),
        fontSize = 24.sp,
        textAlign = TextAlign.Left,
        modifier = modifier,
    )
}