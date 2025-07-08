package com.itsthe1.webexoplayer.api

data class ApiResponse(
    val success: Boolean,
    val deviceid: Int?,
    val message: String?,
    val device: DeviceInfo? = null,
)

data class DeviceInfo(
    val device_id: Int?,
    val device_uid: String?,
    val device_name: String?,
    val device_type: String?,
    val device_mac: String?,
    val device_ip: String?,
    val device_os: String?,
    val device_access_key: String?,
    val device_private_key: String?,
    val device_status: Int?,
    val app_id: Int?,
    val hotel_id: Int?,
    val guest_id: Int?,
    val room_id: String?,
    val room_number: String?,
    val guest: GuestInfo?,
    val greeting: GreetingInfo?,
    val channel: List<ChannelInfo>?
    
)

data class GuestInfo(
    val guest_id: Int?,
    val guest_uid: String?,
    val guest_title: String?,
    val guest_first_name: String?,
    val guest_last_name: String?,
    val guest_checkin_datetime: String?,
    val guest_checkout_datetime: String?,
    val guest_status: String?,
    val greeting_id: String?,
    val language_id: String?,
    val hotel_id: String?

)
data class GreetingInfo(
    val greeting_id: Int?,
    val greeting_text: String?,
    val language_id: String?,
    val hotel_id: String?
)

data class ChannelInfo(
    val channel_id: Int?,
    val channel_name: String?,
    val channel_trans_name: String?,
    val channel_icon: String?,
    val channel_cover: String?,
    val channel_intro: String?,
    val channel_trailer: String?,
    val channel_src: String?,
    val channel_number: String?,
    val channel_status: String?,
    val channel_type_id : String?,
    val hotel_id  : String?,

)