package com.itsthe1.webexoplayer

import android.content.Context
import android.content.SharedPreferences

object RoomManager {
    private const val PREFS_NAME = "WebExoPlayerPrefs"
    private const val KEY_ROOM_NUMBER = "room_number"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun getRoomNumber(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ROOM_NUMBER, null)
    }
    
    fun saveRoomNumber(context: Context, roomNumber: String) {
        getSharedPreferences(context).edit().putString(KEY_ROOM_NUMBER, roomNumber).apply()
    }
    
    fun clearRoomNumber(context: Context) {
        getSharedPreferences(context).edit().remove(KEY_ROOM_NUMBER).apply()
    }
    
    fun hasRoomNumber(context: Context): Boolean {
        return getRoomNumber(context) != null
    }
} 