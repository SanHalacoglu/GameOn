package com.example.gameon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.Blue
import com.example.gameon.ui.theme.BlueDark
import com.example.gameon.ui.theme.BlueLight

@Composable
fun SimpleHeader(title: String, subtitle: String? = null) {
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
                text = title,
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
                text = title,
                color = Blue,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 36.sp,
                    drawStyle = Stroke(0.5F)
                )
            )
        }
        subtitle?.let {
            Text(
                text = it,
                color = Blue,
                style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 24.sp,
                )
            )
        }
    }
}