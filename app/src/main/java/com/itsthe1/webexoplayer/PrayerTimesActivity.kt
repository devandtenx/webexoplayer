package com.itsthe1.webexoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.itsthe1.webexoplayer.api.MuslimSalatRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class PrayerTimesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route_key = intent.getStringExtra("route_key") ?: "KEY_PRAYER_TIME"
        setContent {
            WebExoPlayerTheme {
                PrayerTimesPage(route_key)
            }
        }
    }
}

@Composable
fun PrayerTimesPage(route_key: String) {
    var prayers by remember { mutableStateOf<List<PrayerTime>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val bgImage = remember { DeviceManager.getRouteBackgroundImageByKey(context, route_key) }
    val bgImageUrl = "http://${AppGlobals.webViewURL}/admin-portal/assets/uploads/Backgrounds/$bgImage"
    val city = "riyadh" // Hardcoded city

    // Fetch prayer times from API for the hardcoded city
    LaunchedEffect(city) {
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                val call = MuslimSalatRetrofitClient.instance.getPrayerTimes(
                    location = city.replace(" ", "-").lowercase(Locale.getDefault()),
                    apiKey = AppGlobals.muslimsalatAPIKey
                )
                call.execute()
            }
            if (response.isSuccessful && response.body() != null) {
                val prayerData = response.body()!!
                if (prayerData.items.isNotEmpty()) {
                    val todayPrayers = prayerData.items[0]
                    prayers = listOf(
                        PrayerTime("Fajr", todayPrayers.fajr, "http://${AppGlobals.webViewURL}/one-tv/themes/default/default/prayer/fajr.svg"),
                        PrayerTime("Dhuhr", todayPrayers.dhuhr, "http://${AppGlobals.webViewURL}/one-tv/themes/default/default/prayer/dhuhr.svg"),
                        PrayerTime("Asr", todayPrayers.asr, "http://${AppGlobals.webViewURL}/one-tv/themes/default/default/prayer/asr.svg"),
                        PrayerTime("Maghrib", todayPrayers.maghrib, "http://${AppGlobals.webViewURL}/one-tv/themes/default/default/prayer/maghrib.svg"),
                        PrayerTime("Isha", todayPrayers.isha, "http://${AppGlobals.webViewURL}/one-tv/themes/default/default/prayer/isha.svg")
                    )
                } else {
                    error = "No prayer times available"
                }
            } else {
                error = "Failed to fetch prayer times: ${response.code()}"
            }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AsyncImage(
            model = bgImageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC888888))
        ) {}
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBarCustom()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Prayer Time by muslimsalat.com",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            if (isLoading) {
                Text("Loading prayer times...", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            } else if (error != null) {
                Text(error!!, color = Color.Red, style = MaterialTheme.typography.bodyLarge)
            } else if (prayers.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    prayers.forEach { prayer -> PrayerCard(prayer) }
                }
            }
        }
    }
}


@Composable
fun PrayerCard(prayer: PrayerTime) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    Card(
        modifier = Modifier
            .height(300.dp)
            .size(width = 140.dp, height = 300.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF444444)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = prayer.name, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(prayer.imageUrl)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = prayer.name,
                modifier = Modifier.size(64.dp)
            )
            Text(text = prayer.time, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}

data class PrayerTime(val name: String, val time: String, val imageUrl: String) 