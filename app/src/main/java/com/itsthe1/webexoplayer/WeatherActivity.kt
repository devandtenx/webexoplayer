package com.itsthe1.webexoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import coil.compose.AsyncImage
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.itsthe1.webexoplayer.TopAppBarCustom
import androidx.compose.ui.text.style.TextOverflow

// Data class for weather forecast
data class WeatherForecast(
    val dayOfWeek: String,
    val date: String,
    val description: String,
    val iconUrl: String, // URL for weather icon
    val highTemp: String,
    val lowTemp: String
)

@Composable
fun WeatherCard(forecast: WeatherForecast) {
    Card(
        modifier = Modifier
            .width(175.dp)
            .height(320.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC222222)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                forecast.dayOfWeek,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                forecast.date,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            AsyncImage(
                model = forecast.iconUrl,
                contentDescription = forecast.description,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                forecast.description,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "${forecast.highTemp} / ${forecast.lowTemp}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WeatherPage(forecasts: List<WeatherForecast>, route_key: String) {
    val context = LocalContext.current
    val bgImage = remember { DeviceManager.getRouteBackgroundImageByKey(context, route_key) }
    val bgImageUrl = "http://${AppGlobals.webViewURL}/admin-portal/assets/uploads/Backgrounds/$bgImage"

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image
        AsyncImage(
            model = bgImageUrl,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Foreground content (weather UI)
        Column(
            modifier = Modifier
                .fillMaxSize(), // Overlay for readability
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBarCustom()
            Spacer(Modifier.height(16.dp))
            Text(
                "Forecast by AccuWeather",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                forecasts.forEach { forecast ->
                    WeatherCard(forecast)
                }
            }
        }
    }
}

class WeatherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route_key = intent.getStringExtra("route_key") ?: "KEY_WEATHER"
        val iconUrl = "http://192.168.56.1/one-tv/themes/default/default/weather/2.png"
        val sampleForecasts = listOf(
            WeatherForecast("Tuesday", "18 March", "Fog in the a.m.; mostly sunny", iconUrl, "28.5°", "18.6°"),
            WeatherForecast("Wednesday", "19 March", "Sunny and delightful", iconUrl, "29.5°", "19.6°"),
            WeatherForecast("Thursday", "20 March", "Sunny and very warm", iconUrl, "31.6°", "22.6°"),
            WeatherForecast("Friday", "21 March", "Hot with plenty of sun", iconUrl, "33.6°", "24.9°"),
            WeatherForecast("Saturday", "22 March", "Brilliant sunshine and hot", iconUrl, "34.3°", "25.1°")
        )
        setContent {
            WebExoPlayerTheme {
                WeatherPage(sampleForecasts, route_key) // Replace "your_route_key_here" with the actual route key
            }
        }
    }
} 