package com.example.gameon

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView

class DiscordWebViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val discordUrl = intent.getStringExtra("DiscordLoginUrl")!!

        enableEdgeToEdge()
        setContent {
            WebViewScreen(this@DiscordWebViewActivity, discordUrl)
        }
    }
}

@Composable
fun WebViewScreen(context: Context, discordUrl: String) {
    val webView = rememberWebView(context, discordUrl)

    AndroidView(
        factory = { webView },
        update = { it.loadUrl(discordUrl) }
    )
}

@Composable
fun rememberWebView(context: Context, url: String): WebView {
    return remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    request?.url?.let { uri ->
                        if (uri.toString().startsWith("gameon://callback_discord")) {
                            val code = uri.getQueryParameter("code")
                            code?.let {
                                val intent = Intent(context, AuthActivity::class.java).apply {
                                    putExtra("Code", it)
                                    putExtra("DiscordLoginUrl", url)
                                }
                                context.startActivity(intent)
                                (context as? Activity)?.finish()
                            }
                            return true
                        }
                    }
                    return false
                }
            }
        }
    }
}

