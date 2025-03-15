package com.example.gameon.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gameon.R
import com.example.gameon.ui.theme.DiscordBlurple

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
            .testTag("login_button")
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