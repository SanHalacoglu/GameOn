package com.example.gameon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.gameon.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = BlueDarker),
                verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Logo()
                LoginButton({
                    startActivity(Intent(
                        this@MainActivity,
                        PreferencesActivity::class.java
                    ))
                })
            }
        }
    }
}

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
            painterResource(R.drawable.gameonheadphones),
            "GameOn Headphones",
            modifier = Modifier
                .width(51.dp)
                .height(38.dp)
                .offset(76.dp, 20.dp),
        )
    }
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = DiscordBlurple
        ),
        modifier = Modifier
            .width(250.dp)
            .height(40.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painterResource(R.drawable.discord_icon),
                "Discord Icon",
                modifier = Modifier
                    .width(26.dp)
                    .height(20.dp)
            )
            Text(
                "Sign in with Discord",
                fontFamily = FontFamily(Font(R.font.lato_bold)),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = BlueDarker),
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Logo()
        LoginButton({})
    }
}