package com.itsthe1.webexoplayer

import android.content.Context
import android.content.SharedPreferences
import com.itsthe1.webexoplayer.api.DeviceInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DeviceManager {
    private const val PREFS_NAME = "WebExoPlayerPrefs"
    private const val KEY_ROOM_NUMBER = "room_number"
    private const val KEY_DEVICE_ID = "device_id"
    private const val KEY_DEVICE_UID = "device_uid"
    private const val KEY_DEVICE_NAME = "device_name"
    private const val KEY_DEVICE_TYPE = "device_type"
    private const val KEY_DEVICE_MAC = "device_mac"
    private const val KEY_DEVICE_IP = "device_ip"
    private const val KEY_DEVICE_OS = "device_os"
    private const val KEY_DEVICE_ACCESS_KEY = "device_access_key"
    private const val KEY_DEVICE_PRIVATE_KEY = "device_private_key"
    private const val KEY_DEVICE_STATUS = "device_status"
    private const val KEY_APP_ID = "app_id"
    private const val KEY_HOTEL_ID = "hotel_id"
    private const val KEY_GUEST_ID = "guest_id"
    private const val KEY_ROOM_ID = "room_id"
    private const val KEY_CHANNEL_ID = "channel_id"
    private const val KEY_CHANNEL_TRANS_NAME = "channel_trans_name"
    private const val KEY_CHANNEL_NAME = "channel_name"
    private const val KEY_CHANNEL_ICON = "channel_icon"
    private const val KEY_CHANNEL_COVER = "channel_cover"
    private const val KEY_CHANNEL_INTRO = "channel_intro"
    private const val KEY_CHANNEL_TRAILER = "channel_trailer"
    private const val KEY_CHANNEL_SRC = "channel_src"
    private const val KEY_CHANNEL_NUMBER = "channel_number"
    private const val KEY_CHANNEL_STATUS = "channel_status"
    private const val KEY_CHANNEL_TYPE_ID = "channel_type_id"
    private const val KEY_CHANNEL_HOTEL_ID = "channel_hotel_id"
    private const val KEY_GUEST_UID = "guest_uid"
    private const val KEY_GUEST_TITLE = "guest_title"
    private const val KEY_GUEST_FIRST_NAME = "guest_first_name"
    private const val KEY_GUEST_LAST_NAME = "guest_last_name"
    private const val KEY_GUEST_CHECKIN_DATETIME = "guest_checkin_datetime"
    private const val KEY_GUEST_CHECKOUT_DATETIME = "guest_checkout_datetime"
    private const val KEY_GUEST_STATUS = "guest_status"
    private const val KEY_GREETING_ID = "greeting_id"
    private const val KEY_LANGUAGE_ID = "language_id"
    private const val KEY_GUEST_HOTEL_ID = "guest_hotel_id"
    private const val KEY_GREETING_ID_INT = "greeting_id_int"
    private const val KEY_GREETING_TEXT = "greeting_text"
    private const val KEY_GREETING_LANGUAGE_ID = "greeting_language_id"
    private const val KEY_GREETING_HOTEL_ID = "greeting_hotel_id"
    private const val KEY_ALL_CHANNELS = "all_channels"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveDeviceInfo(context: Context, deviceInfo: DeviceInfo?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_DEVICE_ID, deviceInfo?.device_id?.toString())
        editor.putString(KEY_DEVICE_UID, deviceInfo?.device_uid)
        editor.putString(KEY_DEVICE_NAME, deviceInfo?.device_name)
        editor.putString(KEY_DEVICE_TYPE, deviceInfo?.device_type)
        editor.putString(KEY_DEVICE_MAC, deviceInfo?.device_mac)
        editor.putString(KEY_DEVICE_IP, deviceInfo?.device_ip)
        editor.putString(KEY_DEVICE_OS, deviceInfo?.device_os)
        editor.putString(KEY_DEVICE_ACCESS_KEY, deviceInfo?.device_access_key)
        editor.putString(KEY_DEVICE_PRIVATE_KEY, deviceInfo?.device_private_key)
        editor.putString(KEY_DEVICE_STATUS, deviceInfo?.device_status?.toString())
        editor.putString(KEY_APP_ID, deviceInfo?.app_id?.toString())
        editor.putString(KEY_HOTEL_ID, deviceInfo?.hotel_id?.toString())
        editor.putString(KEY_GUEST_ID, deviceInfo?.guest_id?.toString())
        editor.putString(KEY_ROOM_ID, deviceInfo?.room_id)
        editor.putString(KEY_ROOM_NUMBER, deviceInfo?.room_number)
        editor.apply()
        saveGuestInfo(context, deviceInfo?.guest)
        saveGreetingInfo(context, deviceInfo?.greeting)
        saveAllChannels(context, deviceInfo?.channel ?: emptyList())
    }

    fun getDeviceInfo(context: Context): com.itsthe1.webexoplayer.api.DeviceInfo {
        val prefs = getSharedPreferences(context)
        return com.itsthe1.webexoplayer.api.DeviceInfo(
            device_id = prefs.getString(KEY_DEVICE_ID, null)?.toIntOrNull(),
            device_uid = prefs.getString(KEY_DEVICE_UID, null),
            device_name = prefs.getString(KEY_DEVICE_NAME, null),
            device_type = prefs.getString(KEY_DEVICE_TYPE, null),
            device_mac = prefs.getString(KEY_DEVICE_MAC, null),
            device_ip = prefs.getString(KEY_DEVICE_IP, null),
            device_os = prefs.getString(KEY_DEVICE_OS, null),
            device_access_key = prefs.getString(KEY_DEVICE_ACCESS_KEY, null),
            device_private_key = prefs.getString(KEY_DEVICE_PRIVATE_KEY, null),
            device_status = prefs.getString(KEY_DEVICE_STATUS, null)?.toIntOrNull(),
            app_id = prefs.getString(KEY_APP_ID, null)?.toIntOrNull(),
            hotel_id = prefs.getString(KEY_HOTEL_ID, null)?.toIntOrNull(),
            guest_id = prefs.getString(KEY_GUEST_ID, null)?.toIntOrNull(),
            room_id = prefs.getString(KEY_ROOM_ID, null),
            room_number = prefs.getString(KEY_ROOM_NUMBER, null),
            guest = getGuestInfo(context),
            greeting = getGreetingInfo(context),
            channel = getAllChannels(context)
        )
    }

    fun saveGuestInfo(context: Context, guestInfo: com.itsthe1.webexoplayer.api.GuestInfo?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_GUEST_ID, guestInfo?.guest_id?.toString())
        editor.putString(KEY_GUEST_UID, guestInfo?.guest_uid)
        editor.putString(KEY_GUEST_TITLE, guestInfo?.guest_title)
        editor.putString(KEY_GUEST_FIRST_NAME, guestInfo?.guest_first_name)
        editor.putString(KEY_GUEST_LAST_NAME, guestInfo?.guest_last_name)
        editor.putString(KEY_GUEST_CHECKIN_DATETIME, guestInfo?.guest_checkin_datetime)
        editor.putString(KEY_GUEST_CHECKOUT_DATETIME, guestInfo?.guest_checkout_datetime)
        editor.putString(KEY_GUEST_STATUS, guestInfo?.guest_status)
        editor.putString(KEY_GREETING_ID, guestInfo?.greeting_id)
        editor.putString(KEY_LANGUAGE_ID, guestInfo?.language_id)
        editor.putString(KEY_GUEST_HOTEL_ID, guestInfo?.hotel_id)
        editor.apply()
    }

    fun getGuestInfo(context: Context): com.itsthe1.webexoplayer.api.GuestInfo? {
        val prefs = getSharedPreferences(context)
        val guestId = prefs.getString(KEY_GUEST_ID, null)?.toIntOrNull()
        val guestUid = prefs.getString(KEY_GUEST_UID, null)
        val guestTitle = prefs.getString(KEY_GUEST_TITLE, null)
        val guestFirstName = prefs.getString(KEY_GUEST_FIRST_NAME, null)
        val guestLastName = prefs.getString(KEY_GUEST_LAST_NAME, null)
        val guestCheckinDatetime = prefs.getString(KEY_GUEST_CHECKIN_DATETIME, null)
        val guestCheckoutDatetime = prefs.getString(KEY_GUEST_CHECKOUT_DATETIME, null)
        val guestStatus = prefs.getString(KEY_GUEST_STATUS, null)
        val greetingId = prefs.getString(KEY_GREETING_ID, null)
        val languageId = prefs.getString(KEY_LANGUAGE_ID, null)
        val hotelId = prefs.getString(KEY_GUEST_HOTEL_ID, null)

        // If all fields are null, return null
        if (guestId == null && guestUid == null && guestTitle == null && guestFirstName == null && guestLastName == null && guestCheckinDatetime == null && guestCheckoutDatetime == null && guestStatus == null && greetingId == null && languageId == null && hotelId == null) {
            return null
        }

        return com.itsthe1.webexoplayer.api.GuestInfo(
            guest_id = guestId,
            guest_uid = guestUid,
            guest_title = guestTitle,
            guest_first_name = guestFirstName,
            guest_last_name = guestLastName,
            guest_checkin_datetime = guestCheckinDatetime,
            guest_checkout_datetime = guestCheckoutDatetime,
            guest_status = guestStatus,
            greeting_id = greetingId,
            language_id = languageId,
            hotel_id = hotelId
        )
    }

    private fun saveGreetingInfo(context: Context, greetingInfo: com.itsthe1.webexoplayer.api.GreetingInfo?) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_GREETING_ID, greetingInfo?.greeting_id?.toString())
        editor.putString(KEY_GREETING_TEXT, greetingInfo?.greeting_text)
        editor.putString(KEY_GREETING_LANGUAGE_ID, greetingInfo?.language_id)
        editor.putString(KEY_GREETING_HOTEL_ID, greetingInfo?.hotel_id)
        editor.apply()
    }

    // Remove saveChannelInfo and getChannelInfo, as they are no longer needed

    fun getGreetingInfo(context: Context): com.itsthe1.webexoplayer.api.GreetingInfo? {
        val prefs = getSharedPreferences(context)
        val greetingId = prefs.getString(KEY_GREETING_ID, null)?.toIntOrNull()
        return if (greetingId != null) {
            com.itsthe1.webexoplayer.api.GreetingInfo(
                greeting_id = greetingId,
                greeting_text = prefs.getString(KEY_GREETING_TEXT, null),
                language_id = prefs.getString(KEY_GREETING_LANGUAGE_ID, null),
                hotel_id = prefs.getString(KEY_GREETING_HOTEL_ID, null)
            )
        } else null
    }

    fun getChannelInfo(context: Context): com.itsthe1.webexoplayer.api.ChannelInfo? {
        val prefs = getSharedPreferences(context)
        val channelId = prefs.getString(KEY_CHANNEL_ID, null)?.toIntOrNull()
        return if (channelId != null) {
            com.itsthe1.webexoplayer.api.ChannelInfo(
                channel_id = channelId,
                channel_trans_name = prefs.getString(KEY_CHANNEL_TRANS_NAME, null),
                channel_name = prefs.getString(KEY_CHANNEL_NAME, null),

                channel_icon = prefs.getString(KEY_CHANNEL_ICON, null),
                channel_cover = prefs.getString(KEY_CHANNEL_COVER, null),
                channel_intro = prefs.getString(KEY_CHANNEL_INTRO, null),
                channel_trailer = prefs.getString(KEY_CHANNEL_TRAILER, null),
                channel_src = prefs.getString(KEY_CHANNEL_SRC, null),
                channel_number = prefs.getString(KEY_CHANNEL_NUMBER, null),
                channel_status = prefs.getString(KEY_CHANNEL_STATUS, null),
                channel_type_id = prefs.getString(KEY_CHANNEL_TYPE_ID, null),
                hotel_id = prefs.getString(KEY_CHANNEL_HOTEL_ID, null)
            )
        } else null
    }

    fun getGreetingText(context: Context): String? {
        return getGreetingInfo(context)?.greeting_text
    }

    fun getGuestFullName(context: Context): String? {
        val guest = getGuestInfo(context)
        if (guest == null) return null
        val parts = listOfNotNull(guest.guest_title, guest.guest_first_name, guest.guest_last_name)
        return if (parts.isNotEmpty()) parts.joinToString(" ").trim() else null
    }

    fun getRoomNumber(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_ROOM_NUMBER, null)
    }

    // Save all channels to SharedPreferences
    fun saveAllChannels(context: Context, channels: List<com.itsthe1.webexoplayer.api.ChannelInfo>) {
        val editor = getSharedPreferences(context).edit()
        val gson = Gson()
        val channelsJson = gson.toJson(channels)
        editor.putString(KEY_ALL_CHANNELS, channelsJson)
        editor.apply()
    }

    // Get all channels from SharedPreferences
    fun getAllChannels(context: Context): List<com.itsthe1.webexoplayer.api.ChannelInfo> {
        val prefs = getSharedPreferences(context)
        val channelsJson = prefs.getString(KEY_ALL_CHANNELS, null)
        
        return if (channelsJson != null) {
            try {
                val gson = Gson()
                val type = object : TypeToken<List<com.itsthe1.webexoplayer.api.ChannelInfo>>() {}.type
                gson.fromJson(channelsJson, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Clear all channels from SharedPreferences
    fun clearAllChannels(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_ALL_CHANNELS)
        editor.apply()
    }

    // Debug function to print current channels
    fun debugPrintChannels(context: Context) {
        val channels = getAllChannels(context)
        println("DEBUG: Found ${channels.size} channels in SharedPreferences:")
        channels.forEach { channel ->
            println("  - Channel ${channel.channel_number}: ${channel.channel_trans_name}")
        }
    }
} 