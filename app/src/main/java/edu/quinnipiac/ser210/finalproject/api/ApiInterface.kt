package edu.quinnipiac.ser210.finalproject.api

import edu.quinnipiac.ser210.finalproject.model.Game
import edu.quinnipiac.ser210.finalproject.model.MLBGameResponse
import edu.quinnipiac.ser210.finalproject.model.MLBScoreResponse
import edu.quinnipiac.ser210.finalproject.model.OddsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {

    @GET("/getMLBScoresOnly")
    suspend fun getGames(
        @Query("gameDate") gameDate: String,
        @Query("topPerformers") topPerformers: Boolean = true,
    ): Response<MLBScoreResponse>

    @GET("/getMLBBettingOdds")
    suspend fun getBettingOdds(
        @Query("gameDate") gameDate: String,
        @Query("playerProps") playerProps: Boolean = true,
        @Query("itemFormat") itemFormat: String = "list"
    ): Response<OddsResponse>

    companion object {
        private const val BASE_URL = "https://tank01-mlb-live-in-game-real-time-statistics.p.rapidapi.com/"

        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}
