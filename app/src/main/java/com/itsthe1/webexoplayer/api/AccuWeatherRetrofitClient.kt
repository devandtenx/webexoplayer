package com.itsthe1.webexoplayer.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AccuWeatherRetrofitClient {
    private const val BASE_URL = "http://dataservice.accuweather.com/"

    val instance: AccuWeatherService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(AccuWeatherService::class.java)
    }
} 