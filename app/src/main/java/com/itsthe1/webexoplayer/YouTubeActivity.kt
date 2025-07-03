package com.itsthe1.webexoplayer

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import androidx.compose.ui.viewinterop.AndroidView

class YouTubeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebExoPlayerTheme {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.pluginState = WebSettings.PluginState.ON
                            loadUrl("https://www.youtube.com/") // Load YouTube homepage
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
} 