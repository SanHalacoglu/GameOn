package com.example.gameon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.checkLoggedIn
import com.example.gameon.composables.LoadingScreen
import kotlinx.coroutines.launch

class StartupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoadingScreen()
        }

        lifecycleScope.launch {
            checkLoggedIn(this@StartupActivity)
        }
    }
}

