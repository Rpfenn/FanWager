package edu.quinnipiac.ser210.finalproject.network

import edu.quinnipiac.ser210.finalproject.model.Game
import retrofit2.Response
import retrofit2.http.GET

interface TankApiService {
    @GET("mlb/scores")
    suspend fun getGames(): Response<Map<String, Game>>
}
