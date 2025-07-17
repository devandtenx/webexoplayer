package com.itsthe1.webexoplayer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.itsthe1.webexoplayer.api.ChannelInfo
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import kotlinx.coroutines.delay
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videoplayer.player.VideoView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.Icon
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import android.content.Context
import android.net.wifi.WifiManager

// Data class for channel (local UI model)
data class Channel(
    val number: Int,
    val name: String,
    val iconUrl: String?,
    val streamUrl: String?
)

fun ChannelInfo.toChannel(): Channel {
    val baseUrl = "http://${AppGlobals.webViewURL}/admin-portal/assets/uploads/Channels/Channel/"
    val iconUrl = if (!channel_icon.isNullOrBlank()) baseUrl + channel_icon else null
    return Channel(
        number = channel_number?.toIntOrNull() ?: 0,
        name = channel_name ?: "Unknown Channel",
        iconUrl = iconUrl,
        streamUrl = channel_src
    )
}

class TVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                            arguments = listOf(navArgument("index") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val index = backStackEntry.arguments?.getInt("index") ?: 0
                            ChannelDetailScreen(index = index, navController = navController)
                        }
                    }
                }
            }
        }

        if (DeviceManager.getAllChannels(this).isEmpty()) {
            Toast.makeText(this, "No channels available", Toast.LENGTH_LONG).show()
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
        modifier = Modifier.fillMaxSize().padding(8.dp),
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
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.key) {
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = channel.iconUrl,
                contentDescription = channel.name,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.White),
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
                modifier = Modifier.size(32.dp).background(Color.White, shape = CircleShape).border(1.dp, Color(0xFFB0B0B0), shape = CircleShape),
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
        modifier = Modifier.fillMaxSize().background(Color(0xFF2C3E50)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("No Channels Available", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Please check your connection or contact support", color = Color(0xFFBDC3C7), fontSize = 16.sp)
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
    val currentChannel = channelList.getOrNull(currentIndex)
    var visible by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(false) }
    var selectedControlIndex by remember { mutableStateOf(1) } // 0=Prev, 1=Play/Pause, 2=Next, 3=Go Live
    val controlRequesters = remember { List(4) { FocusRequester() } }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    // MulticastLock for UDP
    var multicastLock by remember { mutableStateOf<WifiManager.MulticastLock?>(null) }

    // Acquire MulticastLock when playing UDP, release when not
    LaunchedEffect(currentChannel?.streamUrl, isPlaying) {
        val isUdp = currentChannel?.streamUrl?.startsWith("udp://") == true
        if (isUdp && isPlaying) {
            if (multicastLock == null) {
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                multicastLock = wifiManager?.createMulticastLock("webexoplayer-udp")?.apply {
                    setReferenceCounted(true)
                    acquire()
                }
            }
        } else {
            multicastLock?.let {
                if (it.isHeld) it.release()
                multicastLock = null
            }
        }
    }
    DisposableEffect(currentChannel?.streamUrl) {
        onDispose {
            multicastLock?.let {
                if (it.isHeld) it.release()
                multicastLock = null
            }
        }
    }

    LaunchedEffect(currentIndex) {
        visible = true
        delay(2000)
        visible = false
        isPlaying = true // auto-play on channel change
        showControls = false
        selectedControlIndex = 1 // default to Play/Pause
    }

    // Hide controls after 2 seconds when resuming playback
    LaunchedEffect(isPlaying) {
        if (isPlaying && showControls) {
            delay(2000)
            showControls = false
        }
    }

    // Request focus for the selected control when controls are shown
    LaunchedEffect(showControls, selectedControlIndex) {
        if (showControls) {
            controlRequesters[selectedControlIndex].requestFocus()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black).focusRequester(focusRequester).focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    if (showControls) {
                        when (event.key) {
                            Key.DirectionRight -> {
                                selectedControlIndex = (selectedControlIndex + 1) % 4
                                true
                            }
                            Key.DirectionLeft -> {
                                selectedControlIndex = (selectedControlIndex + 3) % 4
                                true
                            }
                            Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> {
                                when (selectedControlIndex) {
                                    0 -> { // Prev
                                        currentIndex = if (currentIndex - 1 >= 0) currentIndex - 1 else channelList.lastIndex
                                        showControls = true
                                    }
                                    1 -> { // Play/Pause
                                        isPlaying = !isPlaying
                                        showControls = true
                                    }
                                    2 -> { // Next
                                        currentIndex = (currentIndex + 1) % channelList.size
                                        showControls = true
                                    }
                                    3 -> { // Go Live
                                        currentIndex = channelList.lastIndex
                                        isPlaying = true
                                        showControls = true
                                    }
                                }
                                true
                            }
                            Key.Back -> {
                                showControls = false
                                true
                            }
                            else -> false
                        }
                    } else {
                        when (event.key) {
                            Key.DirectionRight -> { currentIndex = (currentIndex + 1) % channelList.size; true }
                            Key.DirectionLeft -> { currentIndex = if (currentIndex - 1 >= 0) currentIndex - 1 else channelList.lastIndex; true }
                            Key.Back -> {
                                navController.popBackStack()
                                navController.navigate("channelGrid?selectedIndex=$currentIndex")
                                true
                            }
                            Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> {
                                showControls = true
                                true
                            }
                            else -> false
                        }
                    }
                } else false
            }
    ) {
        currentChannel?.streamUrl?.let { streamUrl ->
            key(currentIndex) {
                AndroidView(
                    factory = { ctx ->
                        val videoView = VideoView(ctx)
                        val controller = StandardVideoController(ctx)
                        controller.addDefaultControlComponent(currentChannel.name, false)
                        videoView.setVideoController(controller)
                        if (streamUrl.startsWith("udp://")) {
                            videoView.setPlayerFactory(IjkPlayerFactory.create())
                        }
                        videoView.setUrl(streamUrl)
                        videoView.start()
                        videoViewRef = videoView // store reference
                        videoView
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { videoView ->
                        videoViewRef = videoView // update reference
                        if (isPlaying) {
                            videoView.start()
                        } else {
                            videoView.pause()
                        }
                    }
                )
            }
        } ?: run {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Stream Available", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Show controls overlay when paused or when showControls is true
        AnimatedVisibility(
            visible = showControls || !isPlaying,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xAA222222), RoundedCornerShape(32.dp))
                    .padding(vertical = 8.dp, horizontal = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Channel
                    androidx.compose.material3.IconButton(
                        onClick = {
                            currentIndex = if (currentIndex - 1 >= 0) currentIndex - 1 else channelList.lastIndex
                            showControls = true
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .focusRequester(controlRequesters[0])
                            .onFocusChanged { }
                            .then(
                                if (selectedControlIndex == 0) {
                                    val scale by animateFloatAsState(targetValue = 1.15f)
                                    Modifier
                                        .scale(scale)
                                        .shadow(20.dp, CircleShape, ambientColor = Color(0xFF2196F3), spotColor = Color(0xFF2196F3))
                                        .background(Color(0x552196F3), CircleShape)
                                } else {
                                    val scale by animateFloatAsState(targetValue = 1f)
                                    Modifier.scale(scale)
                                }
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous Channel",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    // Play/Pause
                    androidx.compose.material3.IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                            showControls = true
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .focusRequester(controlRequesters[1])
                            .onFocusChanged { }
                            .then(
                                if (selectedControlIndex == 1) {
                                    val scale by animateFloatAsState(targetValue = 1.15f)
                                    Modifier
                                        .scale(scale)
                                        .shadow(20.dp, CircleShape, ambientColor = Color(0xFF2196F3), spotColor = Color(0xFF2196F3))
                                        .background(Color(0x552196F3), CircleShape)
                                } else {
                                    val scale by animateFloatAsState(targetValue = 1f)
                                    Modifier.scale(scale)
                                }
                            )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    // Next Channel
                    androidx.compose.material3.IconButton(
                        onClick = {
                            currentIndex = (currentIndex + 1) % channelList.size
                            showControls = true
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .focusRequester(controlRequesters[2])
                            .onFocusChanged { }
                            .then(
                                if (selectedControlIndex == 2) {
                                    val scale by animateFloatAsState(targetValue = 1.15f)
                                    Modifier
                                        .scale(scale)
                                        .shadow(20.dp, CircleShape, ambientColor = Color(0xFF2196F3), spotColor = Color(0xFF2196F3))
                                        .background(Color(0x552196F3), CircleShape)
                                } else {
                                    val scale by animateFloatAsState(targetValue = 1f)
                                    Modifier.scale(scale)
                                }
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next Channel",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    // Go Live (seek to live edge)
                    androidx.compose.material3.IconButton(
                        onClick = {
                            videoViewRef?.let { player ->
                                val duration = player.duration
                                val currentPosition = player.currentPosition
                                Log.d("TVActivity", "Go Live: duration=$duration, currentPosition=$currentPosition")
                                if (duration > 0) {
                                    player.seekTo(duration)
                                    isPlaying = true
                                } else {
                                    Toast.makeText(context, "Go Live not supported for this channel.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            showControls = true
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .focusRequester(controlRequesters[3])
                            .onFocusChanged { }
                            .then(
                                if (selectedControlIndex == 3) {
                                    val scale by animateFloatAsState(targetValue = 1.15f)
                                    Modifier
                                        .scale(scale)
                                        .shadow(20.dp, CircleShape, ambientColor = Color(0xFF2196F3), spotColor = Color(0xFF2196F3))
                                        .background(Color(0x552196F3), CircleShape)
                                } else {
                                    val scale by animateFloatAsState(targetValue = 1f)
                                    Modifier.scale(scale)
                                }
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LiveTv,
                            contentDescription = "Go Live",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        currentChannel?.let {
            AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
                Column(
                    modifier = Modifier.align(Alignment.TopStart).padding(24.dp).background(Color(0xCC222222), RoundedCornerShape(12.dp)).padding(20.dp)
                ) {
                    Text("Channel ${it.number}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(it.name, color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}