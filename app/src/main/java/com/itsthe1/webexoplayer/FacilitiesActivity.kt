package com.itsthe1.webexoplayer

import HtmlText
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

class FacilitiesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val route_key = intent.getStringExtra("route_key") ?: "KEY_FACILITIES"
        setContent {
            FacilitiesScreen(route_key)
        }
    }
}

@Composable
fun FacilitiesScreen(routeKey: String) {
    val context = LocalContext.current
    var selectedIndex by remember { mutableStateOf(0) }
    val facilities = DeviceManager.getAllFacilities(context)
    val facilityNames = facilities.map { it.facility_name ?: "Unknown" }
    val facilitySlogan = facilities.getOrNull(selectedIndex)?.facility_slogan ?: ""
    val sliderRaw = facilities.getOrNull(selectedIndex)?.facility_slider ?: "[]"
    val sliderItems = remember(sliderRaw) {
        try {
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<List<String>>>() {}.type
            gson.fromJson<List<List<String>>>(sliderRaw, type)
        } catch (e: Exception) {
            emptyList()
        }
    }.filter { it.getOrNull(1) != "0" }
    val bgImage = remember { DeviceManager.getRouteBackgroundImageByKey(context, routeKey) }
    val bgImageUrl = "http://${AppGlobals.webViewURL}/admin-portal/assets/uploads/Backgrounds/$bgImage"

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
    val listState = rememberLazyListState()
    LaunchedEffect(selectedIndex) { listState.animateScrollToItem(selectedIndex) }
    var currentImageIndex by remember(selectedIndex) { mutableStateOf(0) }
    LaunchedEffect(sliderItems, currentImageIndex) {
        if (sliderItems.isNotEmpty()) {
            kotlinx.coroutines.delay(2500)
            currentImageIndex = (currentImageIndex + 1) % sliderItems.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionUp -> {
                            if (selectedIndex > 0) selectedIndex--
                            true
                        }
                        Key.DirectionDown -> {
                            if (selectedIndex < facilityNames.lastIndex) selectedIndex++
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .focusRequester(focusRequester)
            .focusable()
    ) {
        if (bgImage != null) {
            AsyncImage(
                model = bgImageUrl,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF090909)))
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBarCustom()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // LEFT PANEL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.Transparent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 24.dp)
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Up arrow
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (selectedIndex == 0) Color(0x55222222) else Color(0xCC222222))
                                .clickable(enabled = selectedIndex > 0) {
                                    if (selectedIndex > 0) selectedIndex--
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↑", color = Color.White, fontSize = 28.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        // Facility buttons
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(facilityNames) { idx, name ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xCC222222))
                                        .border(
                                            width = if (selectedIndex == idx) 4.dp else 0.dp,
                                            color = if (selectedIndex == idx) Color(0xFFFF9800) else Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        // Down arrow
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (selectedIndex == facilityNames.lastIndex) Color(0x55222222) else Color(0xCC222222))
                                .clickable(enabled = selectedIndex < facilityNames.lastIndex) {
                                    if (selectedIndex < facilityNames.lastIndex) selectedIndex++
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("↓", color = Color.White, fontSize = 28.sp)
                        }
                    }
                }
                // CENTER PANEL
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .border(4.dp, Color(0xFFFF9800), RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x88222222)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            HtmlText(
                                html = "<span style=\"font-size:28px;\">${facilitySlogan}</span>"
                            )
                        } else {
                            Text(
                                text = facilitySlogan,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                // RIGHT PANEL
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .border(4.dp, Color(0xFFFF9800), RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x88222222)),
                    contentAlignment = Alignment.Center
                ) {
                    if (sliderItems.isNotEmpty()) {
                        val imageName = sliderItems.getOrNull(currentImageIndex)?.getOrNull(0) ?: ""
                        val imageUrl = if (imageName.isNotEmpty()) {
                            "http://192.168.56.1/admin-portal/assets/uploads/Facilities/Sliders/$imageName"
                        } else ""
                        Box(
                            modifier = Modifier
                                .aspectRatio(0.7f)
                                .fillMaxHeight(0.85f)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Text(text = "No Image", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
