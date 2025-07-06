package com.itsthe1.webexoplayer

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme

class YouTubeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WebExoPlayerTheme {
                AndroidView(
    factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            settings.mediaPlaybackRequiresUserGesture = false

            // Force user-agent for Android TV
            settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 9; Nexus Player) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36"

            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()

            // Load YouTube TV
            loadUrl("https://www.youtube.com/tv")
        }
    },
    modifier = Modifier.fillMaxSize()
)

            }
        }
    }
}
