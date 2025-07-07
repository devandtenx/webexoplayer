package com.itsthe1.webexoplayer.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("index.php/android/device_data/adddevice")
    fun addDevice(
        @Field("device_name") deviceName: String,
        @Field("device_uid") deviceId: String,
        @Field("device_mac") macAddress: String,
        @Field("room_number") roomId: String,
        @Field("device_status") deviceStatus: Int,
        @Field("device_os") deviceOs: String,
        @Field("device_type") deviceType: String,
        @Field("device_access_key") deviceAccessKey: String,
        @Field("device_private_key") devicePrivateKey: String,
        @Field("app_id") appId: Int,
    ): Call<ApiResponse>
}
