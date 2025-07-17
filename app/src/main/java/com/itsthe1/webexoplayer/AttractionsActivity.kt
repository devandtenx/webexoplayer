package com.itsthe1.webexoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import androidx.annotation.RequiresApi

class AttractionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route_key = intent.getStringExtra("route_key") ?: "KEY_ATTRACTIONS"
        setContent {
            AttractionsScreen(route_key)
        }
    }
}

@Composable
fun AttractionsScreen(routeKey: String) {
    val context = LocalContext.current
    // Fetch attractions from DeviceManager
    val attractions = remember {
        DeviceManager.getAllAttractions(context)
    }
    // Extract location names and slogans
    val locationNames = attractions.map { it.attraction_name ?: "Unknown" }
    val attractionSlogan = attractions.firstOrNull()?.attraction_slogan ?: "No Slogan"
    // Parse slider images (simulate the structure you described)
    val sliderRaw = attractions.firstOrNull()?.attraction_slider ?: "[]"
    val sliderItems = remember(sliderRaw) {
        // Parse JSON string to List<List<String>>
        try {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<List<String>>>() {}.type
            gson.fromJson<List<List<String>>>(sliderRaw, type)
        } catch (e: Exception) {
            emptyList()
        }
    }.filter { it.getOrNull(1) == "1" }

    // Background image logic
    val bgImage = remember {
        DeviceManager.getRouteBackgroundImageByKey(context, routeKey)
    }
    val bgImageUrl = "http://${AppGlobals.webViewURL}/admin-portal/assets/uploads/Backgrounds/$bgImage"

    Box(modifier = Modifier.fillMaxSize()) {
        if (bgImage != null) {
            AsyncImage(
                model = bgImageUrl,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEEE8DD)))
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBarCustom()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                // Left: Vertical scrollable location names
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC222222)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        locationNames.forEach { name ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xAA111111)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Center: Slogan
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33222222)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            HtmlText(html = attractionSlogan)
                        } else {
                            Text(
                                text = attractionSlogan,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Right: Attraction slider (carousel)
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33222222)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        sliderItems.forEach { item ->
                            val imageName = item.getOrNull(0) ?: ""
                            val imageUrl = if (imageName.isNotEmpty()) {
                                // Replace with your actual image base URL
                                "http://your-server-url.com/path/$imageName"
                            } else ""
                            Card(
                                modifier = Modifier
                                    .size(220.dp, 320.dp)
                                    .padding(end = 16.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                            ) {
                                if (imageUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "No Image", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 