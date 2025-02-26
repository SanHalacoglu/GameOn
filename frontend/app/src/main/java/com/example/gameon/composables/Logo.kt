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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gameon.R
import com.example.gameon.ui.theme.Blue
import com.example.gameon.ui.theme.BlueLight

@Composable
fun Logo(
    large: Boolean = false
) {
    val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
    val fontSize = if (large) 96.sp else 40.sp
    val blurRadius = if (large) 40F else 18F
    val iconWidth = if (large) 51.dp else 20.75.dp
    val offsetX = if (large) 76.dp else 31.5.dp
    val offsetY = if (large) 20.dp else 8.5.dp

    Box (
        contentAlignment = Alignment.TopCenter,
    ) {
        //Text fill and shadow
        Text(
            text = "GameOn",
            color = BlueLight,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = fontSize,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Blue,
                    blurRadius = blurRadius
                ),
            )
        )
        //Text stroke
        Text(
            text = "GameOn",
            color = Blue,
            style = TextStyle(
                fontFamily = fontFamily,
                fontSize = fontSize,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                drawStyle = Stroke(2F)
            )
        )
        Image(
            painterResource(R.drawable.gameon_headphones),
            "GameOn Headphones",
            modifier = Modifier
                .width(iconWidth)
                .offset(offsetX, offsetY),
        )
    }
}

@Preview
@Composable
fun LogoPreview() {
    Logo(false)
}