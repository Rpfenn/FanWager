package edu.quinnipiac.ser210.finalproject.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response

object ApiClient {

    private const val BASE_URL = "https://tank01-mlb-live-in-game-real-time-statistics.p.rapidapi.com"

    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-RapidAPI-Key", "a4a83495ddmshf7e0965c9e681e9p14c029jsn3aaf07be0b5c")
            .addHeader("X-RapidAPI-Host", "tank01-mlb-live-in-game-real-time-statistics.p.rapidapi.com")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService: ApiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }
}