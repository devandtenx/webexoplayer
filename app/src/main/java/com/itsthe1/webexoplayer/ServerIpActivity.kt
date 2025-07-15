package com.itsthe1.webexoplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*

class ServerIpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If server config is already set, skip this page
        if (DeviceManager.hasServerConfiguration(this)) {
            val intent = Intent(this, RoomSelectionActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContent {
            WebExoPlayerTheme {
                ServerIpScreen(
                    onSubmit = { url, resolution, appId, resolutionValue ->
                        // Save the server configuration to SharedPreferences
                        DeviceManager.saveServerConfiguration(
                            context = this@ServerIpActivity,
                            serverUrl = url,
                            resolution = resolution,
                            appId = appId,
                            resolutionValue = resolutionValue
                        )
                        
                        // Also update AppGlobals for backward compatibility
                        AppGlobals.webViewURL = url
                        AppGlobals.resolution = resolution
                        AppGlobals.appId = appId
                        AppGlobals.resolutionValue = resolutionValue
                        
                        // Navigate to RoomSelectionActivity
                        val intent = Intent(this@ServerIpActivity, RoomSelectionActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun ServerIpScreen(
    onSubmit: ((String, String, Int, Int) -> Unit)? = null // url, resolution, appId
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // Focus requesters for each control
    val resolution720FocusRequester = remember { FocusRequester() }
    val resolution1080FocusRequester = remember { FocusRequester() }
    val app2FocusRequester = remember { FocusRequester() }
    val app5FocusRequester = remember { FocusRequester() }
    val saveButtonFocusRequester = remember { FocusRequester() }

    // Focus state for each control
    var is720Focused by remember { mutableStateOf(false) }
    var is1080Focused by remember { mutableStateOf(false) }
    var isApp2Focused by remember { mutableStateOf(false) }
    var isApp5Focused by remember { mutableStateOf(false) }
    var isSaveFocused by remember { mutableStateOf(false) }

    // State for form
    var url by remember { mutableStateOf("") }
    var selectedResolution by remember { mutableStateOf("720p") }
    var selectedAppId by remember { mutableStateOf(2) }
    var isError by remember { mutableStateOf(false) }

    // Set initial focus to the first radio button (720p)
    LaunchedEffect(Unit) {
        resolution720FocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Server Configuration",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 18.dp)
                    )
                    OutlinedTextField(
                        value = url,
                        onValueChange = {
                            url = it
                            isError = it.isBlank()
                        },
                        label = { Text("Server URL", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp) },
                        placeholder = { Text("e.g., http://10.0.1.28", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFA000),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                            focusedLabelColor = Color(0xFFFFA000),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                            cursorColor = Color(0xFFFFA000),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        isError = isError,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp)
                            .background(Color.Transparent)
                    )
                    if (isError) {
                        Text(
                            text = "Please enter a valid server URL",
                            color = Color(0xFFFF5252),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Select Resolution",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .focusRequester(resolution720FocusRequester)
                                .focusable()
                                .onFocusChanged { is720Focused = it.isFocused }
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown) {
                                        when (it.key) {
                                            Key.DirectionRight -> { resolution1080FocusRequester.requestFocus(); true }
                                            Key.DirectionDown -> { app2FocusRequester.requestFocus(); true }
                                            Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> { selectedResolution = "720p"; true }
                                            else -> false
                                        }
                                    } else false
                                }
                                .then(
                                    if (is720Focused) Modifier
                                        .border(4.dp, Color.Red, RoundedCornerShape(50))
                                        .background(Color(0x33FF0000), RoundedCornerShape(50))
                                    else Modifier
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = selectedResolution == "720p",
                                onClick = { selectedResolution = "720p" }
                            )
                        }
                        Text("720p", color = Color.White, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                        Box(
                            modifier = Modifier
                                .focusRequester(resolution1080FocusRequester)
                                .focusable()
                                .onFocusChanged { is1080Focused = it.isFocused }
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown) {
                                        when (it.key) {
                                            Key.DirectionLeft -> { resolution720FocusRequester.requestFocus(); true }
                                            Key.DirectionDown -> { app5FocusRequester.requestFocus(); true }
                                            Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> { selectedResolution = "1080p"; true }
                                            else -> false
                                        }
                                    } else false
                                }
                                .then(
                                    if (is1080Focused) Modifier
                                        .border(4.dp, Color.Red, RoundedCornerShape(50))
                                        .background(Color(0x33FF0000), RoundedCornerShape(50))
                                    else Modifier
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = selectedResolution == "1080p",
                                onClick = { selectedResolution = "1080p" }
                            )
                        }
                        Text("1080p", color = Color.White, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Text(
                        text = "Select an App",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .focusRequester(app2FocusRequester)
                                .focusable()
                                .onFocusChanged { isApp2Focused = it.isFocused }
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown) {
                                        when (it.key) {
                                            Key.DirectionRight -> { app5FocusRequester.requestFocus(); true }
                                            Key.DirectionUp -> { resolution720FocusRequester.requestFocus(); true }
                                            Key.DirectionDown -> { saveButtonFocusRequester.requestFocus(); true }
                                            Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> { selectedAppId = 2; true }
                                            else -> false
                                        }
                                    } else false
                                }
                                .then(
                                    if (isApp2Focused) Modifier
                                        .border(4.dp, Color.Red, RoundedCornerShape(50))
                                        .background(Color(0x33FF0000), RoundedCornerShape(50))
                                    else Modifier
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = selectedAppId == 2,
                                onClick = { selectedAppId = 2 }
                            )
                        }
                        Text("Smart TV App", color = Color.White, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                        Box(
                            modifier = Modifier
                                .focusRequester(app5FocusRequester)
                                .focusable()
                                .onFocusChanged { isApp5Focused = it.isFocused }
                                .onKeyEvent {
                                    if (it.type == KeyEventType.KeyDown) {
                                        when (it.key) {
                                            Key.DirectionLeft -> { app2FocusRequester.requestFocus(); true }
                                            Key.DirectionUp -> { resolution1080FocusRequester.requestFocus(); true }
                                            Key.DirectionDown -> { saveButtonFocusRequester.requestFocus(); true }
                                            Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> { selectedAppId = 5; true }
                                            else -> false
                                        }
                                    } else false
                                }
                                .then(
                                    if (isApp5Focused) Modifier
                                        .border(4.dp, Color.Red, RoundedCornerShape(50))
                                        .background(Color(0x33FF0000), RoundedCornerShape(50))
                                    else Modifier
                                )
                                .padding(4.dp)
                        ) {
                            RadioButton(
                                selected = selectedAppId == 5,
                                onClick = { selectedAppId = 5 }
                            )
                        }
                        Text("Android TV App", color = Color.White, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Save Button with focus highlight
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(saveButtonFocusRequester)
                            .focusable()
                            .onFocusChanged { isSaveFocused = it.isFocused }
                            .onKeyEvent {
                                if (it.type == KeyEventType.KeyDown) {
                                    when (it.key) {
                                        Key.DirectionUp -> { app2FocusRequester.requestFocus(); true }
                                        Key.DirectionLeft -> { app2FocusRequester.requestFocus(); true }
                                        Key.DirectionRight -> { app5FocusRequester.requestFocus(); true }
                                        Key.Enter, Key.NumPadEnter, Key.DirectionCenter -> {
                                            if (url.isNotBlank()) {
                                                onSubmit?.invoke(url, selectedResolution, selectedAppId, if (selectedResolution == "720p") 720 else 1080)
                                            } else {
                                                isError = true
                                            }
                                            true
                                        }
                                        else -> false
                                    }
                                } else false
                            }
                            .then(
                                if (isSaveFocused) Modifier
                                    .border(4.dp, Color.Red, RoundedCornerShape(24.dp))
                                    .background(Color(0x33FF0000), RoundedCornerShape(24.dp))
                                else Modifier
                            )
                    ) {
                        Button(
                            onClick = {
                                if (url.isNotBlank()) {
                                    onSubmit?.invoke(url, selectedResolution, selectedAppId, if (selectedResolution == "720p") 720 else 1080)
                                } else {
                                    isError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(24.dp),
                            enabled = url.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (url.isNotBlank()) Color(0xFFFFA000) else Color.Gray,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Save",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
