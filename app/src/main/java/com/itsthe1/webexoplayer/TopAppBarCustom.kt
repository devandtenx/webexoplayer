package com.itsthe1.webexoplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

@Composable
fun TopAppBarCustom() {
    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("dd  EEEE, MMMM", Locale.getDefault()) }
    var currentTime by remember { mutableStateOf(timeFormatter.format(Date())) }
    var currentDate by remember { mutableStateOf(dateFormatter.format(Date())) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTime = timeFormatter.format(now)
            currentDate = dateFormatter.format(now)
            delay(1000)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF333333).copy(alpha = 0.85f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo and Room
            Row(verticalAlignment = Alignment.CenterVertically) {
                val context = LocalContext.current
                val logoFileName = DeviceManager.getHotelLogo(context)
                val logoUrl = if (!logoFileName.isNullOrBlank()) {
                    "http://192.168.56.1/admin-portal/assets/uploads/Logos/" + logoFileName
                } else null
                if (!logoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = "Hotel Logo",
                        modifier = Modifier.size(140.dp),
                        // You can adjust size as needed
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    // Fallback to text logo if no image
                    Text(
                        text = "10",
                        color = Color(0xFFFF2D2D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "X",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "TECHNOLOGIES",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
            // Center: Welcome message and Route info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Welcome message
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB0B0B0).copy(alpha = 0.85f), shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = DeviceManager.getGuestFullName(LocalContext.current) ?: "",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Route information
                RouteInfoDisplay()
            }
            // Room number to the right of welcome message
            Text(
                text = DeviceManager.getRoomNumber(LocalContext.current) ?: "",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp)
            )
            // Right: Time, Date, and Reset button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                // Time and Date
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currentTime,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = currentDate,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RouteInfoDisplay() {
    val context = LocalContext.current
    val mainRoutes = DeviceManager.getMainRoutes(context)
    val homeChildRoutes = DeviceManager.getHomeChildRoutes(context)
    
    if (mainRoutes.isNotEmpty()) {
        val currentRoute = mainRoutes.firstOrNull { it.route_name == "HOME" }
        if (currentRoute != null) {
            Text(
                text = "Current: ${currentRoute.route_name} (${homeChildRoutes.size} options)",
                color = Color(0xFF00FF00),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
} 