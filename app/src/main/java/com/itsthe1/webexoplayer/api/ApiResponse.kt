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
    val channel: List<ChannelInfo>?,
    val hotel: HostelInfo?,
    val routes: List<RouteInfo>?,
    val promotions: List<PromotionInfo>? = null,
    val attractions: List<AttractionInfo>? = null
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

data class HostelInfo(
    val hotel_id :Int?,
    val hotel_name :String?,
    val hotel_logo :String?,
    val hotel_covers :String?,
    val hotel_intro :String?,
    val hotel_about :String?,
    val hotel_address :String?,
    val hotel_social :String?,
)

data class RouteInfo(
    val route_id: Int?,
    val route_name: String?,
    val route_key: String?,
    val route_attr: String?,
    val route_callback: String?,
    val route_controller: String?,
    val route_icon: String?,
    val route_bg_type: Int?,
    val route_music_type: Int?,
    val route_music: String?,
    val route_bg: String?, // Changed from List<RouteBackground>? to String? (JSON string)
    val route_parent_id: Int?,
    val route_visibility: Int?,
    val display_order: Int?,
    val app_id: Int?,
    val hotel_id: Int?,
    val language_id: Int?,
    val child_routes: List<RouteInfo>?

)

data class RouteBackground(
    val image: String?,
    val isActive: String?
)

data class PromotionInfo(
    val promotion_id: Int?,
    val promotion_title: String?,
    val promotion_src: String?,
    val promotion_link: String?,
    val promotion_delay: Int?,
    val promotion_status: Int?,
    val display_order: Int?,
    val translation_of: Int?,
    val promo_type_id: Int?,
    val promo_holder_id: Int?,
    val app_id: Int?,
    val hotel_id: Int?,
    val language_id: Int?,
    val promo_playlist_id: Int?
)

data class AttractionInfo(
    val attraction_id: Int?,
    val attraction_icon: String?,
    val attraction_name: String?,
    val attraction_subtitle: String?,
    val attraction_slogan: String?,
    val attraction_slider: String?, // JSON string or List<List<String>> if you want to parse
    val attraction_intro: String?,
    val attraction_expire_date: String?,
    val is_bookable: Int?,
    val display_order: Int?,
    val translation_of: Int?,
    val hotel_id: Int?,
    val language_id: Int?,
    val deleted: Int?,
    val deleted_by: String?,
    val created_on: String?,
    val created_by: String?,
    val modified_on: String?,
    val modified_by: String?
)