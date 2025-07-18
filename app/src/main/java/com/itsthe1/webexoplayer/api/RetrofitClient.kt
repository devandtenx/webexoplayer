package com.itsthe1.webexoplayer.api

import com.itsthe1.webexoplayer.AppGlobals
import com.itsthe1.webexoplayer.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val BASE_URL = "http://${AppGlobals.webViewURL}/admin-portal/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
