package edu.quinnipiac.ser210.finalproject.api


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiInterface {

    // 1. Fetch current menu with product IDs
    @GET("us/currentMenu")
    suspend fun getCurrentMenu(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String
    ): Response<McdMenuResponse>

    // 2. Fetch full product details by ID
    @GET("us/products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int,
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String
    ): Response<McdItem>

    companion object {
        private const val BASE_URL = "https://mcdonald-s-products-api.p.rapidapi.com/"

        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}
