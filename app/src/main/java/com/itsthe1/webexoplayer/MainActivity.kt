package com.itsthe1.webexoplayer

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
import androidx.compose.foundation.BorderStroke
import androidx.tv.material3.Card as TvCard
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import androidx.tv.material3.Text as TvText
import androidx.tv.material3.Surface as TvSurface
import androidx.tv.material3.CardDefaults as TvCardDefaults


class MainActivity : ComponentActivity() {
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
                            model = "https://s56442.cdn.ngenix.net/img/0/0/resize/rshb/agrolife/1647277344_10-kartinkin-net-p-kartinki-otel-11.jpg",
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
                            TopAppBarCustom(
                                welcomeText = "Welcome Mr. Ajul K Jose",
                                room = "Room 101"
                            )
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
                                        Text(
                                            text = "Dear Guest,\n\nWe glad to welcome you and are always focused on achieving excellence through our service. For any assistance, please contact the reception and we will be happy to help. You may review us online or drop your comments in the suggestion box placed at the reception. Thank you for allowing us the pleasure of being your hosts. Have stay is a memorable one.\n\nYour Sincerely,\nGeneral Manager",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(24.dp))
                                // Right Card: Ad Image with overlay text
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(260.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        AsyncImage(
                                            model = "https://cdn.pixabay.com/photo/2016/03/05/19/02/chicken-1239424_1280.jpg",
                                            contentDescription = "Ad",
                                            modifier = Modifier.matchParentSize(),
                                            contentScale = ContentScale.FillBounds
                                        )
                                        // Overlay image fills the card
                                        AsyncImage(
                                            model = "https://assets.bwbx.io/images/users/iqjWHBFdfxIU/iLdmwJ3yOq0s/v1/1200x800.jpg",
                                            contentDescription = "Ad Overlay Image",
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .matchParentSize(),
                                            contentScale = ContentScale.FillBounds
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            // 3. Bottom Menu Row
                            TVMenuRow(onMenuSelected = { _, label ->
                                if (label == "YOUTUBE") {
                                    val intent = Intent(this@MainActivity, YouTubeActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this@MainActivity, MenuDetailActivity::class.java)
                                    intent.putExtra("label", label)
                                    startActivity(intent)
                                }
                            })
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
            modifier = Modifier.fillMaxSize(),
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
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TVMenuRow(onMenuSelected: (Int, String) -> Unit) {
    val menuOptions = listOf(
        MenuOption("TV", Icons.Filled.Tv),
        MenuOption("SPECIAL OFFERS", Icons.Filled.LocalOffer),
        MenuOption("YOUTUBE", Icons.Filled.PlayArrow),
        MenuOption("BROWSER", Icons.Filled.Language),
        MenuOption("Connect", Icons.Filled.Cable),
        MenuOption("Messages", Icons.Filled.Email),
        MenuOption("IN-ROOM DINING", Icons.Filled.Restaurant)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        itemsIndexed(menuOptions) { index, option ->
            MenuButton(
                label = option.label,
                icon = option.icon,
                selected = false,
                onClick = { onMenuSelected(index, option.label) }
            )
        }
    }
}