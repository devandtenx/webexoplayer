package com.itsthe1.webexoplayer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme

// Data class for channel (local UI model)
data class Channel(
    val number: Int,
    val name: String,
    val iconUrl: String?
)

// Extension function to convert ChannelInfo to Channel
fun com.itsthe1.webexoplayer.api.ChannelInfo.toChannel(): Channel {
    val baseUrl = "http://192.168.56.1/admin-portal/assets/uploads/Channels/Channel/"
    val iconUrl = if (!channel_icon.isNullOrBlank()) baseUrl + channel_icon else null
    return Channel(
        number = channel_number?.toIntOrNull() ?: 0,
        name = channel_name ?: "Unknown Channel",
        iconUrl = iconUrl
    )
}

class TVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Debug: Print current channels
        DeviceManager.debugPrintChannels(this)
        
        setContent {
            WebExoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val channels = remember {
                        DeviceManager.getAllChannels(this@TVActivity)
                            .map { it.toChannel() }
                            .sortedBy { it.number }
                    }
                    
                    if (channels.isNotEmpty()) {
                        Column {
                            TopAppBarCustom()
                            
                            ChannelGrid(channels)
                        }
                    } else {
                        // Show empty state when no channels are available
                        EmptyChannelsState()
                        // Show toast message
                        LaunchedEffect(Unit) {
                            Toast.makeText(
                                this@TVActivity,
                                "No channels available",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
    
    
}

@Composable
fun ChannelGrid(channelList: List<Channel>) {
    val focusRequesters = remember { List(channelList.size) { FocusRequester() } }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(channelList.size) { index ->
            ChannelCard(
                channel = channelList[index],
                focusRequester = focusRequesters[index],
                onMoveFocus = { direction ->
                    val nextIndex = when (direction) {
                        MoveDirection.RIGHT -> if (index + 1 < channelList.size) index + 1 else null
                        MoveDirection.LEFT -> if (index - 1 >= 0) index - 1 else null
                        MoveDirection.DOWN -> if (index + 3 < channelList.size) index + 3 else null
                        MoveDirection.UP -> if (index - 3 >= 0) index - 3 else null
                    }
                    nextIndex?.let { focusRequesters[it].requestFocus() }
                }
            )
        }
    }
}

enum class MoveDirection { RIGHT, LEFT, DOWN, UP }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChannelCard(
    channel: Channel,
    focusRequester: FocusRequester,
    onMoveFocus: (MoveDirection) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor = if (isFocused) Color(0xFF007BFF) else Color(0xFFE0E0E0)
    val backgroundColor = if (isFocused) Color(0xFFF8FAFF) else Color(0xFFF4F6F8)

    Box(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionRight -> { onMoveFocus(MoveDirection.RIGHT); true }
                        Key.DirectionLeft -> { onMoveFocus(MoveDirection.LEFT); true }
                        Key.DirectionDown -> { onMoveFocus(MoveDirection.DOWN); true }
                        Key.DirectionUp -> { onMoveFocus(MoveDirection.UP); true }
                        else -> false
                    }
                } else false
            }
            .background(backgroundColor, shape = RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(64.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = channel.iconUrl,
                contentDescription = channel.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = channel.name,
                color = Color(0xFF222222),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White, shape = CircleShape)
                    .border(1.dp, Color(0xFFB0B0B0), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = channel.number.toString(),
                    color = Color(0xFF495057),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun EmptyChannelsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3E50)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Channels Available",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Please check your connection or contact support",
                color = Color(0xFFBDC3C7),
                fontSize = 16.sp
            )
        }
    }
}
