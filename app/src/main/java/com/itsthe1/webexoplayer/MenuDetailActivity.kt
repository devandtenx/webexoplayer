package com.itsthe1.webexoplayer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import android.util.Log

class MenuDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MenuDetailActivity", "Started MenuDetailActivity")
        val label = intent.getStringExtra("route_key") ?: "Sample"
        try {
            setContent {
                WebExoPlayerTheme {
                    MenuDetailPage(label = label, onBack = { finish() })
                }
            }
        } catch (e: Exception) {
            Log.e("MenuDetailActivity", "Compose error: ", e)
            setContent {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading page: ${e.localizedMessage}",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MenuDetailPage(label: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "This is the $label page!",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.clickable { onBack() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA000)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Go Backssss",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
} 