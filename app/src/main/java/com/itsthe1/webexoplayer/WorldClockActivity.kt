package com.itsthe1.webexoplayer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage

class WorldClockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         val route_key = intent.getStringExtra("route_key") ?: "world_clock"
        setContent {
            WebExoPlayerTheme {
                val bgImage = remember {
                    DeviceManager.getRouteBackgroundImageByKey(this@WorldClockActivity, route_key)
                }
                
                val bgImageUrl = "http://${AppGlobals.webViewURL}/admin-portal/assets/uploads/Backgrounds/$bgImage"
                
                Box(modifier = Modifier.fillMaxSize()) {
                    if (bgImageUrl != null) {
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
                        WorldClockRow(onBack = { finish() })
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorldClockRow(onBack: () -> Unit) {
    val cities = listOf(
        Triple("Moscow", "Europe/Moscow", Color(0xFFBFA76F)),
        Triple("New York", "America/New_York", Color(0xFFBFA76F)),
        Triple("Amsterdam", "Europe/Amsterdam", Color(0xFFBFA76F)),
        Triple("Tokyo", "Asia/Tokyo", Color(0xFFBFA76F)),
        Triple("London", "Europe/London", Color(0xFFBFA76F))
    )
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss") }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("dd EEEE") }
    val monthYearFormatter = remember { DateTimeFormatter.ofPattern("MMMM, yyyy") }
    val scrollState = rememberScrollState()

    // For live updating
    var now by remember { mutableStateOf(ZonedDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = ZonedDateTime.now()
            kotlinx.coroutines.delay(1000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        for ((city, zone, cardColor) in cities) {
            val cityTime = now.withZoneSameInstant(ZoneId.of(zone))
            WorldClockCard(
                city = city,
                time = cityTime,
                cardColor = cardColor,
                timeFormatter = timeFormatter,
                dayFormatter = dayFormatter,
                monthYearFormatter = monthYearFormatter
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
    }
    Spacer(modifier = Modifier.height(32.dp))
    // Removed Go Back button to match the attached image
}

@Composable
fun WorldClockCard(
    city: String,
    time: ZonedDateTime,
    cardColor: Color,
    timeFormatter: DateTimeFormatter,
    dayFormatter: DateTimeFormatter,
    monthYearFormatter: DateTimeFormatter
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(340.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4AF37)), // metallic gold
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8B7B5A), shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = city,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            AnalogClock(
                hours = time.hour,
                minutes = time.minute,
                seconds = time.second,
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = time.format(timeFormatter),
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = time.format(dayFormatter),
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = time.format(monthYearFormatter),
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun AnalogClock(hours: Int, minutes: Int, seconds: Int, modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = this.center
        // Draw thick dark border
        drawCircle(
            color = Color(0xFF333333),
            radius = radius,
            center = center,
            style = Stroke(width = 12f)
        )
        // Draw clock face
        drawCircle(
            color = Color.White,
            radius = radius - 8f,
            center = center,
            style = Stroke(width = 4f)
        )
        // Draw hour marks
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val lineLength = if (i % 3 == 0) radius * 0.22f else radius * 0.14f
            val startX = center.x + (radius - lineLength - 8f) * cos(angle).toFloat()
            val startY = center.y + (radius - lineLength - 8f) * sin(angle).toFloat()
            val endX = center.x + (radius - 8f) * cos(angle).toFloat()
            val endY = center.y + (radius - 8f) * sin(angle).toFloat()
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = if (i % 3 == 0) 5f else 2.5f,
                cap = StrokeCap.Round
            )
        }
        // Draw hour hand
        val hourAngle = ((hours % 12) + minutes / 60f) * 30 - 90
        drawHand(center, (radius - 24f) * 0.6f, hourAngle, 10f, Color.Black)
        // Draw minute hand
        val minuteAngle = (minutes + seconds / 60f) * 6 - 90
        drawHand(center, (radius - 24f) * 0.85f, minuteAngle, 7f, Color.Black)
        // Draw second hand
        val secondAngle = (seconds * 6 - 90).toFloat()
        drawHand(center, (radius - 24f) * 0.95f, secondAngle, 3f, Color.Red)
        // Draw center dot
        drawCircle(color = Color.Black, radius = 8f, center = center)
    }
}

fun DrawScope.drawHand(center: androidx.compose.ui.geometry.Offset, length: Float, angleDegrees: Float, strokeWidth: Float, color: Color) {
    val angleRad = Math.toRadians(angleDegrees.toDouble())
    val endX = center.x + length * cos(angleRad).toFloat()
    val endY = center.y + length * sin(angleRad).toFloat()
    drawLine(
        color = color,
        start = center,
        end = androidx.compose.ui.geometry.Offset(endX, endY),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
} 