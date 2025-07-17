package com.itsthe1.webexoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.itsthe1.webexoplayer.api.AccuWeatherRetrofitClient
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny

data class WeatherForecast(
    val dayOfWeek: String,
    val date: String,
    val description: String,
    val iconUrl: String,
    val highTemp: String,
    val lowTemp: String
)

// Helper to format date string
fun formatDate(dateString: String): Pair<String, String> {
    return try {
        val parsedDate = LocalDate.parse(dateString.substring(0, 10))
        val dayOfWeek = parsedDate.format(DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH))
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH))
        Pair(dayOfWeek, formattedDate)
    } catch (e: Exception) {
        Pair("Unknown", dateString)
    }
}

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

    var accuweatherForecasts by remember { mutableStateOf<List<WeatherForecast>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val apiKey = "GUmV0E5oB1FHtp238rxZ33HHyOmUKkK7"
            val city = "Riyadh"
            try {
                val locationResponse = withContext(Dispatchers.IO) {
                    AccuWeatherRetrofitClient.instance.getLocationKey(apiKey, city).execute()
                }
                val locationKey = locationResponse.body()?.firstOrNull()?.Key
                if (locationKey != null) {
                    val forecastResponse = withContext(Dispatchers.IO) {
                        AccuWeatherRetrofitClient.instance.getFiveDayForecast(locationKey, apiKey).execute()
                    }
                    val forecastBody = forecastResponse.body()
                    if (forecastBody != null) {
                        accuweatherForecasts = forecastBody.DailyForecasts.map {
                            val (dayOfWeekFormatted, dateFormatted) = formatDate(it.Date)
                            WeatherForecast(
                                dayOfWeek = dayOfWeekFormatted,
                                date = dateFormatted,
                                description = it.Day.IconPhrase,
                                iconUrl = "https://developer.accuweather.com/sites/default/files/" + it.Day.Icon.toString().padStart(2, '0') + "-s.png",
                                highTemp = "${it.Temperature.Maximum.Value}°${it.Temperature.Maximum.Unit}",
                                lowTemp = "${it.Temperature.Minimum.Value}°${it.Temperature.Minimum.Unit}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // handle error if needed
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = bgImageUrl,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
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
                val displayForecasts = if (accuweatherForecasts.isNotEmpty()) {
                    accuweatherForecasts
                } else {
                    forecasts.map {
                        val (dayOfWeekFormatted, dateFormatted) = formatDate(it.date)
                        it.copy(dayOfWeek = dayOfWeekFormatted, date = dateFormatted)
                    }
                }
                displayForecasts.forEach { forecast ->
                    WeatherCard(forecast)
                }
            }
        }
    }
}

class WeatherActivity : ComponentActivity() {
    private val accuweatherApiKey = "GUmV0E5oB1FHtp238rxZ33HHyOmUKkK7"
    private val defaultCity = "Riyadh"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route_key = intent.getStringExtra("route_key") ?: "KEY_WEATHER"
        setContent {
            WebExoPlayerTheme {
                var forecasts by remember { mutableStateOf<List<WeatherForecast>>(emptyList()) }
                var loading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    loading = true
                    error = null
                    try {
                        val locationResponse = withContext(Dispatchers.IO) {
                            AccuWeatherRetrofitClient.instance.getLocationKey(accuweatherApiKey, defaultCity).awaitResponse()
                        }
                        val locationKey = locationResponse.body()?.firstOrNull()?.Key
                        if (locationKey != null) {
                            val forecastResponse = withContext(Dispatchers.IO) {
                                AccuWeatherRetrofitClient.instance.getFiveDayForecast(locationKey, accuweatherApiKey).awaitResponse()
                            }
                            val forecastBody = forecastResponse.body()
                            if (forecastBody != null) {
                                forecasts = forecastBody.DailyForecasts.map {
                                    val (dayOfWeekFormatted, dateFormatted) = formatDate(it.Date)
                                    WeatherForecast(
                                        dayOfWeek = dayOfWeekFormatted,
                                        date = dateFormatted,
                                        description = it.Day.IconPhrase,
                                        iconUrl = "https://developer.accuweather.com/sites/default/files/" + it.Day.Icon.toString().padStart(2, '0') + "-s.png",
                                        highTemp = "${it.Temperature.Maximum.Value}°",
                                        lowTemp = "${it.Temperature.Minimum.Value}°"
                                    )
                                }
                            } else {
                                error = "No forecast data."
                            }
                        } else {
                            error = "No location key found."
                        }
                    } catch (e: Exception) {
                        error = e.localizedMessage ?: "Unknown error"
                    }
                    loading = false
                }

                when {
                    loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Weather Logo",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(Modifier.height(24.dp))
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error")
                    }
                    else -> WeatherPage(forecasts, route_key)
                }
            }
        }
    }
}
