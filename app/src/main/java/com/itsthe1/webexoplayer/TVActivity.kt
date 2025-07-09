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
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.itsthe1.webexoplayer.api.ChannelInfo
import kotlinx.coroutines.delay
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.key
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player

// Data class for channel (local UI model)
data class Channel(
    val number: Int,
    val name: String,
    val iconUrl: String?,
    val streamUrl: String? // <-- Added streamUrl
)

// Extension function to convert ChannelInfo to Channel
fun ChannelInfo.toChannel(): Channel {
    val baseUrl = "http://192.168.56.1/admin-portal/assets/uploads/Channels/Channel/"
    val iconUrl = if (!channel_icon.isNullOrBlank()) baseUrl + channel_icon else null
    return Channel(
        number = channel_number?.toIntOrNull() ?: 0,
        name = channel_name ?: "Unknown Channel",
        iconUrl = iconUrl,
        streamUrl = channel_src // <-- Make sure this field exists in ChannelInfo
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
                    val navController = rememberNavController()
                    
                    NavHost(navController = navController, startDestination = "channelGrid?selectedIndex={selectedIndex}") {
                        composable(
                            "channelGrid?selectedIndex={selectedIndex}",
                            arguments = listOf(
                                navArgument("selectedIndex") {
                                    type = NavType.IntType
                                    defaultValue = 0
                                }
                            )
                        ) { backStackEntry ->
                            val selectedIndex = backStackEntry.arguments?.getInt("selectedIndex") ?: 0
                            if (channels.isNotEmpty()) {
                                Column {
                                    TopAppBarCustom()
                                    ChannelGrid(channels, navController, selectedIndex)
                                }
                            } else {
                                EmptyChannelsState()
                            }
                        }
                        composable(
                            "detail/{index}",
                            arguments = listOf(
                                navArgument("index") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val index = backStackEntry.arguments?.getInt("index") ?: 0
                            ChannelDetailScreen(index = index, navController = navController)
                        }
                    }
                }
            }
        }
        
        // Show toast if no channels available
        if (DeviceManager.getAllChannels(this).isEmpty()) {
            Toast.makeText(
                this,
                "No channels available",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    
}

@Composable
fun ChannelGrid(channelList: List<Channel>, navController: NavController, selectedIndex: Int = 0) {
    val focusRequesters = remember { List(channelList.size) { FocusRequester() } }
    LaunchedEffect(selectedIndex) {
        if (channelList.isNotEmpty()) {
            focusRequesters[selectedIndex.coerceIn(channelList.indices)].requestFocus()
        }
    }
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
                },
                onClick = {
                    navController.navigate("detail/$index")
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
    onMoveFocus: (MoveDirection) -> Unit,
    onClick: () -> Unit
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
            .height(64.dp)
            .clickable { onClick() },
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

@Composable
fun ChannelDetailScreen(index: Int, navController: NavController) {
    val context = LocalContext.current
    val channelList = remember {
        DeviceManager.getAllChannels(context).map { it.toChannel() }.sortedBy { it.number }
    }
    var currentIndex by remember { mutableStateOf(index) }
    val channel = channelList.getOrNull(currentIndex)
    var visible by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    // ExoPlayer setup - recreate when channel changes
    val exoPlayer = remember(currentIndex) {
        val currentChannel = channelList.getOrNull(currentIndex)
        android.util.Log.d("TVActivity", "Setting up ExoPlayer for channel: ${currentChannel?.name}, streamUrl: ${currentChannel?.streamUrl}")
        
        if (currentChannel?.streamUrl.isNullOrBlank()) {
            android.util.Log.w("TVActivity", "No stream URL available for channel: ${currentChannel?.name}")
            null
        } else {
            try {
                val streamUrl = currentChannel?.streamUrl
                if (streamUrl != null) {
                    ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(streamUrl))
                        prepare()
                        playWhenReady = true
                        addListener(object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                android.util.Log.e("ExoPlayer", "Playback error for channel ${currentChannel?.name}: ${error.message}", error)
                            }
                            
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                android.util.Log.d("ExoPlayer", "Playback state changed to: $playbackState for channel: ${currentChannel?.name}")
                            }
                        })
                    }
                } else {
                    android.util.Log.w("TVActivity", "Stream URL is null for channel: ${currentChannel?.name}")
                    null
                }
            } catch (e: Exception) {
                android.util.Log.e("ExoPlayer", "Failed to create ExoPlayer for channel ${currentChannel?.name}: ${e.message}", e)
                null
            }
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer?.release()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Show overlay for 2 seconds, and show again when channel changes
    LaunchedEffect(currentIndex) {
        visible = true
        delay(2000)
        visible = false
    }

    // Handle channel switching and ExoPlayer updates
    LaunchedEffect(currentIndex) {
        val currentChannel = channelList.getOrNull(currentIndex)
        android.util.Log.d("TVActivity", "Channel changed to index: $currentIndex, channel: ${currentChannel?.name}")
        
        // Force recomposition to update ExoPlayer
        // The remember(currentIndex) should handle this, but we add explicit logging
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C3E50))
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionRight -> {
                            currentIndex = if (currentIndex + 1 < channelList.size) {
                                currentIndex + 1
                            } else {
                                0 // Loop to first channel
                            }
                            true
                        }
                        Key.DirectionLeft -> {
                            currentIndex = if (currentIndex - 1 >= 0) {
                                currentIndex - 1
                            } else {
                                channelList.lastIndex // Loop to last channel
                            }
                            true
                        }
                        Key.Back -> {
                            navController.popBackStack()
                            navController.navigate("channelGrid?selectedIndex=$currentIndex")
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        // ExoPlayer view or fallback
        if (exoPlayer != null) {
            key(currentIndex) { // Force recreation when channel changes
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = false
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Fallback when no stream URL is available
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Stream Available",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "This channel is not available for streaming",
                        color = Color(0xFFBDC3C7),
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Overlay
        channel?.let {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                key(currentIndex) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(24.dp)
                            .background(Color(0xCC222222), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Channel ${it.number}",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = it.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
