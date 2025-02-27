package com.example.gameon.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gameon.R
import com.example.gameon.ui.theme.DiscordBlurple

@Composable
fun Icon() {
    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(
                color = DiscordBlurple,
                shape = CircleShape
            )
    ) {
        Image(
            painterResource(R.drawable.discord_icon),
            "Discord Icon",
            modifier = Modifier
                .width(26.dp)
                .height(20.dp)
        )
    }
}