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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import androidx.compose.material3.Icon

// Data class for weather forecast
data class WeatherForecast(
    val dayOfWeek: String,
    val date: String,
    val description: String,
    val iconRes: Int, // Drawable resource for weather icon
    val highTemp: String,
    val lowTemp: String
)

@Composable
fun WeatherCard(forecast: WeatherForecast) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(260.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF444444))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(forecast.dayOfWeek, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(forecast.date, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Icon(
                painter = painterResource(id = forecast.iconRes),
                contentDescription = forecast.description,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(forecast.description, color = Color.White, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text("${forecast.highTemp} / ${forecast.lowTemp}", color = Color.White, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun WeatherPage(forecasts: List<WeatherForecast>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF888888)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            "Forecast by AccuWeather",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            forecasts.forEach { forecast ->
                WeatherCard(forecast)
            }
        }
    }
}

class WeatherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sampleForecasts = listOf(
            WeatherForecast("Tuesday", "18 March", "Fog in the a.m.; mostly sunny", R.drawable.ic_sunny, "28.5°", "18.6°"),
            WeatherForecast("Wednesday", "19 March", "Sunny and delightful", R.drawable.ic_sunny, "29.5°", "19.6°"),
            WeatherForecast("Thursday", "20 March", "Sunny and very warm", R.drawable.ic_sunny, "31.6°", "22.6°"),
            WeatherForecast("Friday", "21 March", "Hot with plenty of sun", R.drawable.ic_sunny, "33.6°", "24.9°"),
            WeatherForecast("Saturday", "22 March", "Brilliant sunshine and hot", R.drawable.ic_sunny, "34.3°", "25.1°")
        )
        setContent {
            WebExoPlayerTheme {
                WeatherPage(sampleForecasts)
            }
        }
    }
} 