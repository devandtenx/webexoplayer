package com.itsthe1.webexoplayer

import HtmlText
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.itemsIndexed
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Card as TvCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.tv.material3.Surface as TvSurface
import androidx.tv.material3.CardDefaults as TvCardDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        
        setContent {
            WebExoPlayerTheme {
                TvSurface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Background Image
                        AsyncImage(
                            model = "http://192.168.56.1/admin-portal/assets/uploads/Backgrounds/3fd4aacc0d97bb920ad81785d55a89d5@3x.png",
                            contentDescription = "Background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Foreground content
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 1. App Bar at the very top
                            TopAppBarCustom()
                            // 2. Row with two cards (welcome message and ad)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Left Card: Logo + Welcome Message
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(260.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(20.dp)
                                    ) {
                                        // Logo
                                        HtmlText(DeviceManager.getGreetingText(LocalContext.current) ?: "")
                                    }
                                }
                                Spacer(modifier = Modifier.width(24.dp))
                                // Right Card: Routes Information
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(260.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Text(
                                            text = "Routes (Parent ID: 5211)",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        RoutesDisplay()
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            // 3. Bottom Menu Row
                            TVMenuRow(
                                onMenuSelected = { _, routeKey ->
                                    when (routeKey) {
                                        "KEY_YOUTUBE" -> {
                                            val intent = Intent(this@MainActivity, YouTubeActivity::class.java)
                                            startActivity(intent)
                                        }
                                        "KEY_XTV" -> {
                                            val intent = Intent(this@MainActivity, TVActivity::class.java)
                                            startActivity(intent)
                                        }
                                        else -> {
                                            val intent = Intent(this@MainActivity, MenuDetailActivity::class.java)
                                            intent.putExtra("route_key", routeKey)
                                            startActivity(intent)
                                        }
                                    }
                                },
                                initialSelectedIndex = 0
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MenuOption(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MenuButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    TvCard(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(120.dp),
        scale = TvCardDefaults.scale(scale = 1.1f),
        border = TvCardDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                BorderStroke(3.dp, Color(0xFFFFA000))
            )
        ),
        colors = TvCardDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 8.dp), // more padding
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TvText(
                text = label,
                color = Color.White,
                style = TvMaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MenuButtonWithImage(label: String, routeIcon: String?, selected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageLoader = createImageLoader(context)
    
    TvCard(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(120.dp),
        scale = TvCardDefaults.scale(scale = 1.1f),
        border = TvCardDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                BorderStroke(3.dp, Color(0xFFFFA000))
            )
        ),
        colors = TvCardDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 8.dp), // more padding
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!routeIcon.isNullOrBlank()) {
                val iconUrl = "http://192.168.56.1/admin-portal/assets/uploads/Menus/Menu_Icons/$routeIcon"
                println("DEBUG: Loading icon from URL: $iconUrl")
                
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(iconUrl)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = label,
                    modifier = Modifier.size(40.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White), // Tint SVG to white
                    onError = { state ->
                        println("DEBUG: Error loading image for $label: ${state.result}")
                    },
                    onSuccess = { state ->
                        println("DEBUG: Successfully loaded image for $label")
                    }
                )
            } else {
                // Fallback to default icon if no route icon
                println("DEBUG: No route icon provided for $label, using default icon")
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TvText(
                text = label,
                color = Color.White,
                style = TvMaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TVMenuRow(
    onMenuSelected: (Int, String) -> Unit, // Now passes route_key
    initialSelectedIndex: Int = 0
) {
    var selectedIndex by remember { mutableStateOf(initialSelectedIndex) }
    
    val context = LocalContext.current
    val routes = DeviceManager.getRoutesByParentKey(context, "KEY_HOME")
    
    println("DEBUG: Found ${routes.size} routes")
    routes.forEach { route ->
        println("DEBUG: Route: ${route.route_name}, Icon: ${route.route_icon}, Key: ${route.route_key}")
    }
 

    if (routes.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            itemsIndexed(routes) { index, route ->
                MenuButtonWithImage(
                    label = route.route_name ?: "Unknown",
                    routeIcon = route.route_icon,
                    selected = index == selectedIndex,
                    onClick = { 
                        selectedIndex = index
                        onMenuSelected(index, route.route_key ?: "") // Pass route_key instead of label
                    }
                )
            }
        }
    } else {
        // Show a test button with a known working image
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                TestImageButton()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TestImageButton() {
    val context = LocalContext.current
    val imageLoader = createImageLoader(context)
    
    TvCard(
        onClick = { },
        modifier = Modifier
            .width(120.dp)
            .height(120.dp),
        scale = TvCardDefaults.scale(scale = 1.1f),
        border = TvCardDefaults.border(
            focusedBorder = androidx.tv.material3.Border(
                BorderStroke(3.dp, Color(0xFFFFA000))
            )
        ),
        colors = TvCardDefaults.colors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Test with a known working image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("https://picsum.photos/40/40")
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Test Image",
                modifier = Modifier.size(40.dp),
                onError = { state ->
                    println("DEBUG: Error loading test image: ${state.result}")
                },
                onSuccess = { state ->
                    println("DEBUG: Successfully loaded test image")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TvText(
                text = "Test Image",
                color = Color.White,
                style = TvMaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
    }
}


@Composable
fun RoutesDisplay() {
    val context = LocalContext.current
    val routes = DeviceManager.getRoutesByParentId(context, 5211)
    
    println("DEBUG: RoutesDisplay - Found ${routes.size} routes")
    
    if (routes.isEmpty()) {
        Text(
            text = "No routes found for parent ID 5211",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    } else {
        routes.forEach { route ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "• ${route.route_name ?: "Unknown"} (ID: ${route.route_id})",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                if (route.route_key != null) {
                    Text(
                        text = "  Key: ${route.route_key}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (route.route_icon != null) {
                    Text(
                        text = "  Icon: ${route.route_icon}",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (route.route_callback != null) {
                    Text(
                        text = "  Callback: ${route.route_callback}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun createImageLoader(context: android.content.Context): ImageLoader {
    return remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}