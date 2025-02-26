package com.example.gameon.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.Blue
import com.example.gameon.ui.theme.BlueLight

@Composable
fun Logo() {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    Box (
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        //Text fill and shadow
        Text(
            text = "GameOn",
            color = BlueLight,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 96.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Blue,
                    blurRadius = 40F
                ),
            )
        )
        //Text stroke
        Text(
            text = "GameOn",
            color = Blue,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = 96.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                drawStyle = Stroke(2F)
            )
        )
        Image(
            painterResource(R.drawable.gameon_headphones),
            "GameOn Headphones",
            modifier = Modifier
                .width(51.dp)
                .height(38.dp)
                .offset(76.dp, 20.dp),
        )
    }
}