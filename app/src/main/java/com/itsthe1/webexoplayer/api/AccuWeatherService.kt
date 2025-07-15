package com.itsthe1.webexoplayer.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccuWeatherService {
    @GET("locations/v1/cities/search")
    fun getLocationKey(
        @Query("apikey") apiKey: String,
        @Query("q") city: String
    ): Call<List<LocationResponse>>

    @GET("forecasts/v1/daily/5day/{locationKey}")
    fun getFiveDayForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apiKey: String,
        @Query("details") details: Boolean = true,
        @Query("metric") metric: Boolean = true
    ): Call<ForecastResponse>
} 