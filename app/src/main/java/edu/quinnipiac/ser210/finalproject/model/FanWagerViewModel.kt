package edu.quinnipiac.ser210.finalproject

import android.util.Log
import androidx.lifecycle.ViewModel
import edu.quinnipiac.ser210.finalproject.model.MLBGameResponse
import edu.quinnipiac.ser210.finalproject.model.GameDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import com.google.gson.Gson
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FanWagerViewModel : ViewModel() {

    private val _games = MutableStateFlow<List<GameDetails>>(emptyList())
    val games: StateFlow<List<GameDetails>> = _games

    private val client = OkHttpClient()

    // Replace with your actual API key
    private val apiKey = "9702a4a17amsh122c783445bb75ap122f3ajsna301a75ea9ef"

    // Base URL and endpoint
    private val baseUrl = "https://tank01-mlb-live-in-game-real-time-statistics.p.rapidapi.com"
    private val endpoint = "/getMLBGamesForDate"

    fun fetchGames() {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val url = "$baseUrl$endpoint?gameDate=$today"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Key", apiKey)
            .addHeader("X-RapidAPI-Host", "tank01-mlb-live-in-game-real-time-statistics.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TankAPI", "❌ Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("TankAPI", "❌ API Error (${it.code}): ${it.body?.string()}")
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {
                        // 🪵 Debug log for raw JSON
                        Log.d("TankAPI", "🔍 RAW JSON:\n$responseBody")

                        try {
                            val parsed = Gson().fromJson(responseBody, MLBGameResponse::class.java)
                            val gameList = parsed.body
                            _games.value = gameList
                            Log.d("TankAPI", "✅ Loaded ${gameList.size} games.")
                        } catch (ex: Exception) {
                            Log.e("TankAPI", "❌ Exception while parsing JSON: ${ex.message}")
                        }
                    } else {
                        Log.e("TankAPI", "❌ Empty response body")
                    }
                }
            }
        })
    }
}
