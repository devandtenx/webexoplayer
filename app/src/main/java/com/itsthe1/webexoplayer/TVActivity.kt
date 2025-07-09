package com.itsthe1.webexoplayer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.itsthe1.webexoplayer.api.ChannelInfo
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import kotlinx.coroutines.delay
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout

// Data class for channel (local UI model)
data class Channel(
    val number: Int,
    val name: String,
    val iconUrl: String?,
    val streamUrl: String?
)

fun ChannelInfo.toChannel(): Channel {
    val baseUrl = "http://192.168.56.1/admin-portal/assets/uploads/Channels/Channel/"
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

        val vlcInstance = remember {
            try {
                LibVLC(context, mutableListOf("--no-drop-late-frames", "--no-skip-frames", "--rtsp-tcp"))
            } catch (e: Exception) {
                Log.e("VLC", "Failed to create LibVLC: ${e.message}", e)
                throw e
            }
        }

    val mediaPlayer = remember { MediaPlayer(vlcInstance) }

    LaunchedEffect(currentIndex) {
        val channel = channelList.getOrNull(currentIndex)
        if (!channel?.streamUrl.isNullOrBlank()) {
            try {
                mediaPlayer.stop()
                val media = Media(vlcInstance, Uri.parse(channel!!.streamUrl))
                media.addOption(":network-caching=300")
                mediaPlayer.media = media
                media.release()
                mediaPlayer.play()
            } catch (e: Exception) {
                Log.e("VLC", "Error: ${e.message}", e)
            }
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
            vlcInstance.release()
        }
    }

    LaunchedEffect(currentIndex) {
        visible = true
        delay(2000)
        visible = false
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black).focusRequester(focusRequester).focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionRight -> { currentIndex = (currentIndex + 1) % channelList.size; true }
                        Key.DirectionLeft -> { currentIndex = if (currentIndex - 1 >= 0) currentIndex - 1 else channelList.lastIndex; true }
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
        currentChannel?.streamUrl?.let {
            key(currentIndex) {
                AndroidView(
                    factory = {
                        VLCVideoLayout(context).also { layout ->
                            // Detach any existing views before re-attaching
                            try {
                                mediaPlayer.detachViews()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
            
                            mediaPlayer.attachViews(layout, null, false, false)
            
                            layout.layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } ?: run {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Stream Available", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
