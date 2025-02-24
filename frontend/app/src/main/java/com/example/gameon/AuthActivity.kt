package com.example.gameon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.gameon.api.methods.finishLogin
import com.example.gameon.composables.LoadingScreen
import kotlinx.coroutines.launch

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoadingScreen()
        }

        val uri: Uri? = intent.data
        if (uri == null || uri.scheme != "gameon") {
            finish()
            return
        }

        val code = uri.getQueryParameter("code")
        if (code == null) {
            finish()
            return
        }

//        val code = intent.getStringExtra("Code")
//        if (code == null) {
//            val discordUrl = intent.getStringExtra("DiscordLoginUrl")
//            startActivity(
//                Intent(
//                this@AuthActivity, LoginActivity::class.java
//            ).apply { putExtra("DiscordLoginUrl", discordUrl) })
//            finish()
//            return
//        }

        lifecycleScope.launch {
            finishLogin(code, this@AuthActivity)
        }
    }
}