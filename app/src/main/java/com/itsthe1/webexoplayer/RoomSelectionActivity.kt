package com.itsthe1.webexoplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.itsthe1.webexoplayer.ui.theme.WebExoPlayerTheme
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.lifecycle.lifecycleScope
import com.itsthe1.webexoplayer.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.itsthe1.webexoplayer.api.ApiResponse
import android.net.wifi.WifiManager
import android.content.Context

fun getMacAddress(context: Context): String {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    return wifiInfo.macAddress ?: "Unavailable"
}

class RoomSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGlobals.initialize(this)
        // Always try to lookup by device ID, do not check or save room number locally
        val androidId = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: ""
        val context = this
        val macAddress = getMacAddress(context)
        lifecycleScope.launch(Dispatchers.IO) {
            val call = RetrofitClient.instance.lookupDeviceById(androidId)
            call.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    val deviceRoomId = response.body()?.device?.room_id
                    if (response.isSuccessful && response.body()?.success == true && !deviceRoomId.isNullOrBlank()) {
                        // Store room_id in SharedPreferences
                        DeviceManager.saveDeviceInfo(context,response.body()?.device)
                        // Use room_id from device object
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Show room selection UI as fallback
                        runOnUiThread {
                            setContent {
                                WebExoPlayerTheme {
                                    RoomSelectionScreen(
                                        onRoomSelected = { roomNumber, deviceId, macAddress ->
                                            // Call API to save device info
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                val call = RetrofitClient.instance.addDevice(
                                                    deviceName = macAddress, // or any name you want
                                                    deviceId = deviceId,
                                                    macAddress = macAddress, // you can get MAC address if needed
                                                    roomId = roomNumber,
                                                    deviceStatus=1,
                                                    deviceOs="Android",
                                                    deviceType="Android TV",
                                                    deviceAccessKey=" ",
                                                    devicePrivateKey=" ",
                                                    appId=5,
                                                )
                                                call.enqueue(object : Callback<ApiResponse> {
                                                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                                        // Handle success
                                                        if (response.isSuccessful && response.body()?.success == true) {
                                                            val intent = Intent(context, MainActivity::class.java)
                                                            startActivity(intent)
                                                            finish()
                                                        } else {
                                                            // Show error message
                                                            runOnUiThread {
                                                                android.widget.Toast.makeText(
                                                                    applicationContext,
                                                                    response.body()?.message ?: "Failed to register device. Please try again.",
                                                                    android.widget.Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                                        // Show error message
                                                        runOnUiThread {
                                                            android.widget.Toast.makeText(
                                                                applicationContext,
                                                                "Network error: ${t.localizedMessage ?: t.message ?: "Unknown error"}",
                                                                android.widget.Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                })
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Show room selection UI as fallback
                    runOnUiThread {
                        setContent {
                            WebExoPlayerTheme {
                                RoomSelectionScreen(
                                    onRoomSelected = { roomNumber, deviceId, macAddress ->
                                        // Call API to save device info
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            val call = RetrofitClient.instance.addDevice(
                                                deviceName = macAddress, // or any name you want
                                                deviceId = deviceId,
                                                macAddress = macAddress, // you can get MAC address if needed
                                                roomId = roomNumber,
                                                deviceStatus=1,
                                                deviceOs="Android",
                                                deviceType="Android TV",
                                                deviceAccessKey=" ",
                                                devicePrivateKey=" ",
                                                appId=5,
                                            )
                                            call.enqueue(object : Callback<ApiResponse> {
                                                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                                                    // Handle success
                                                    if (response.isSuccessful && response.body()?.success == true) {
                                                        val intent = Intent(context, MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        // Show error message
                                                        runOnUiThread {
                                                            android.widget.Toast.makeText(
                                                                applicationContext,
                                                                response.body()?.message ?: "Failed to register device. Please try again.",
                                                                android.widget.Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                }
                                                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                                                    // Show error message
                                                    runOnUiThread {
                                                        android.widget.Toast.makeText(
                                                            applicationContext,
                                                            "Network error: ${t.localizedMessage ?: t.message ?: "Unknown error"}",
                                                            android.widget.Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            })
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            })
        }
    }
}

@SuppressLint("HardwareIds")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSelectionScreen(onRoomSelected: (String, String, String) -> Unit) {
    var roomNumber by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    // Add state for Change Server IP button
    var isChangeServerIpPressed by remember { mutableStateOf(false) }
    var isChangeServerIpFocused by remember { mutableStateOf(false) }
    val changeServerIpFocusRequester = remember { FocusRequester() }

    // Get Android device ID
    val context = androidx.compose.ui.platform.LocalContext.current
    val androidId = remember {
        Secure.getString(context.contentResolver, Secure.ANDROID_ID) ?: ""
    }
    val macAddress = remember {
        getMacAddress(context)
    }
    val displayId = when {
        androidId.isNullOrBlank() -> "Unavailable on emulator"
        androidId == "9774d56d682e549c" -> "Unavailable on emulator"
        else -> androidId
    }

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
        // Dark blurred overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
        )

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight()
                    .shadow(24.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp, horizontal = 32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    

                    // Welcome Text
                    Text(
                        text = "Welcome to Our Hotel",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Please enter your room number to continue",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Room Number Input
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = {
                            roomNumber = it
                            isError = false
                        },
                        label = { Text("Room Number", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp) },
                        placeholder = { Text("e.g., 101", color = Color.White.copy(alpha = 0.5f), fontSize = 18.sp) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
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
                            .height(64.dp)
                            .background(Color.Transparent)
                    )

                    if (isError) {
                        Text(
                            text = "Please enter a valid room number",
                            color = Color(0xFFFF5252),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    // Device ID below the input
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Device ID: $displayId",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.15f))
                            .padding(6.dp)
                    )

                    // Continue and Change Server IP Buttons in a Row
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        
                        // Change Server IP Button
                        Button(
                            onClick = {
                                isChangeServerIpPressed = true
                                DeviceManager.clearAll(context)
                                val activity = context as? android.app.Activity
                                activity?.let {
                                    val intent = Intent(it, ServerIpActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    it.startActivity(intent)
                                    it.finish()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .focusRequester(changeServerIpFocusRequester)
                                .onFocusChanged { isChangeServerIpFocused = it.isFocused }
                                .focusable(),
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = Color(0xFFFF5252),
                                        shape = RoundedCornerShape(32.dp)
                                    )
                                    .then(
                                        if (isChangeServerIpFocused)
                                            Modifier.border(
                                                width = 4.dp,
                                                color = Color.White,
                                                shape = RoundedCornerShape(32.dp)
                                            )
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Change Server IP",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // Continue Button
                        Button(
                            onClick = {
                                if (roomNumber.isNotBlank()) {
                                    isPressed = true
                                    onRoomSelected(roomNumber, androidId, macAddress)
                                } else {
                                    isError = true
                                    isPressed = false
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .focusRequester(focusRequester)
                                .onFocusChanged { isFocused = it.isFocused }
                                .focusable(),
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = if (isPressed)
                                            Brush.horizontalGradient(
                                                colors = listOf(Color(0xFFFFC107), Color(0xFFFF8A00))
                                            )
                                        else
                                            Brush.horizontalGradient(
                                                colors = listOf(Color(0xFFFF8A00), Color(0xFFFFC107))
                                            ),
                                        shape = RoundedCornerShape(32.dp)
                                    )
                                    .then(
                                        if (isFocused)
                                            Modifier.border(
                                                width = 4.dp,
                                                color = Color.White,
                                                shape = RoundedCornerShape(32.dp)
                                            )
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Continue",
                                    color = Color.Black,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 