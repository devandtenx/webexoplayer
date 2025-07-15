package com.itsthe1.webexoplayer.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MuslimSalatService {
    @GET("{location}/daily.json")
    fun getPrayerTimes(
        @Path("location") location: String,
        @Query("key") apiKey: String
    ): Call<MuslimSalatResponse>
} 