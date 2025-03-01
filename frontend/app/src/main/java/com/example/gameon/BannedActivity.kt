package com.example.gameon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.gameon.ui.theme.*

class BannedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BannedScreen()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BannedScreen() {
        val fontFamily = FontFamily(Font(R.font.barlowcondensed_bold))
        val fontSize = 96.sp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RedDark),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Text(
                    "BANNED",
                    fontSize = fontSize,
                    color = RedLight,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Red,
                            blurRadius = 40F
                        ),
                    )
                )
                Text(
                    "BANNED",
                    color = Red,
                    style = TextStyle(
                        fontFamily = fontFamily,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        drawStyle = Stroke(4F)
                    )
                )
            }
            Text(
                "You are no longer allowed to use GameOn.",
                fontSize = 20.sp,
                fontFamily = fontFamily,
                color = Red
            )
        }
    }
}