package com.itsthe1.webexoplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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

        var showLoader = false
        if (DeviceManager.hasServerConfiguration(this)) {
            showLoader = true
            // Delay the redirect by at least 1000ms
            android.os.Handler(mainLooper).postDelayed({
                val intent = Intent(this, RoomSelectionActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }
        setContent {
            WebExoPlayerTheme {
                var loaderVisible by remember { mutableStateOf(showLoader) }
                // If loaderVisible is true, show loader, else show ServerIpScreen
                if (loaderVisible) {
                    LoaderScreen()
                } else {
                    ServerIpScreen(
                        onSubmit = { url, resolution, appId, resolutionValue ->
                            var fixedUrl = url.trim()
                            if (!fixedUrl.startsWith("http://") && !fixedUrl.startsWith("https://")) {
                                fixedUrl = "http://$fixedUrl"
                            }

                            DeviceManager.saveServerConfiguration(
                                context = this@ServerIpActivity,
                                serverUrl = fixedUrl,
                                resolution = resolution,
                                appId = appId,
                                resolutionValue = resolutionValue
                            )

                            android.widget.Toast.makeText(
                                this@ServerIpActivity,
                                "Server details saved successfully!",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@ServerIpActivity, RoomSelectionActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun ServerIpScreen(
    onSubmit: ((String, String, Int, Int) -> Unit)? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var url by remember { mutableStateOf("http://192.168.56.1") }
    var selectedResolution by remember { mutableStateOf("720p") }
    var selectedAppId by remember { mutableStateOf(2) }
    var isError by remember { mutableStateOf(false) }

    val resolutionOptions = listOf("720p", "1080p")
    val appOptions = listOf(2 to "Smart TV App", 5 to "Android TV App")

    val resolutionFocusStates = remember { resolutionOptions.map { mutableStateOf(false) } }
    val appFocusStates = remember { appOptions.map { mutableStateOf(false) } }

    // Add for button focus
    val buttonFocusRequester = remember { FocusRequester() }
    var isButtonFocused by remember { mutableStateOf(false) }

    // Add FocusRequesters for radio options
    val resolutionFocusRequesters = remember { resolutionOptions.map { FocusRequester() } }
    val appFocusRequesters = remember { appOptions.map { FocusRequester() } }

    // Optionally, request focus for the first option for testing
    LaunchedEffect(Unit) {
        resolutionFocusRequesters[0].requestFocus()
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
                        label = {
                            Text("Server URL", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        },
                        placeholder = {
                            Text("e.g., http://10.0.1.28", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                        },
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
                        resolutionOptions.forEachIndexed { index, res ->
                            val isFocused = resolutionFocusStates[index]
                            val interactionSource = remember { MutableInteractionSource() }
                            val focusRequester = resolutionFocusRequesters[index]

                            Box(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .focusable(true, interactionSource = interactionSource)
                                    .onFocusChanged { focusState ->
                                        isFocused.value = focusState.isFocused
                                    }
                                    .onKeyEvent {
                                        if (it.key == Key.Enter || it.key == Key.NumPadEnter || it.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                                            selectedResolution = res
                                            true
                                        } else false
                                    }
                                    .border(
                                        width = 2.dp,
                                        color = if (isFocused.value) Color.White else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedResolution == res,
                                        onClick = { selectedResolution = res },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFFFFA000),
                                            unselectedColor = Color.White
                                        )
                                    )
                                    Text(res, color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
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
                        appOptions.forEachIndexed { index, (id, name) ->
                            val isFocused = appFocusStates[index]
                            val interactionSource = remember { MutableInteractionSource() }
                            val focusRequester = appFocusRequesters[index]

                            Box(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .focusable(true, interactionSource = interactionSource)
                                    .onFocusChanged { focusState ->
                                        isFocused.value = focusState.isFocused
                                    }
                                    .onKeyEvent {
                                        if (it.key == Key.Enter || it.key == Key.NumPadEnter || it.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                                            selectedAppId = id
                                            true
                                        } else false
                                    }
                                    .border(
                                        width = 2.dp,
                                        color = if (isFocused.value) Color.White else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedAppId == id,
                                        onClick = { selectedAppId = id },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFFFFA000),
                                            unselectedColor = Color.White
                                        )
                                    )
                                    Text(name, color = Color.White, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Wrap the Button in a Box to apply border on focus
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .focusRequester(buttonFocusRequester)
                            .onFocusChanged { isButtonFocused = it.isFocused }
                            .then(
                                if (isButtonFocused)
                                    Modifier.border(
                                        width = 4.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                else Modifier
                            )
                    ) {
                        Button(
                            onClick = {
                                if (url.isNotBlank()) {
                                    onSubmit?.invoke(
                                        url,
                                        selectedResolution,
                                        selectedAppId,
                                        if (selectedResolution == "720p") 720 else 1080
                                    )
                                } else {
                                    isError = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize(),
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

@Composable
fun LoaderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFFA000),
            strokeWidth = 6.dp
        )
    }
}
