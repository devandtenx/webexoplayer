package com.itsthe1.webexoplayer.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MuslimSalatRetrofitClient {
    private const val BASE_URL = "https://muslimsalat.com/"

    val instance: MuslimSalatService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MuslimSalatService::class.java)
    }
} 