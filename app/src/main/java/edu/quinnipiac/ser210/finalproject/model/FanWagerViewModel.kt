package edu.quinnipiac.ser210.finalproject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.quinnipiac.ser210.finalproject.model.GameDetails
import edu.quinnipiac.ser210.finalproject.model.MLBGameResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import com.google.gson.Gson
import edu.quinnipiac.ser210.finalproject.api.ApiClient
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import edu.quinnipiac.ser210.finalproject.model.Game
import edu.quinnipiac.ser210.finalproject.model.LeaderboardEntry
import edu.quinnipiac.ser210.finalproject.model.GameOdds
import edu.quinnipiac.ser210.finalproject.network.RetrofitInstance.api
import kotlinx.coroutines.launch

class FanWagerViewModel : ViewModel() {

    private val _games = MutableStateFlow<List<GameDetails>>(emptyList())
    val games: StateFlow<List<GameDetails>> = _games

    private val _odds = MutableStateFlow<List<GameOdds>>(emptyList())
    val odds: StateFlow<List<GameOdds>> = _odds

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard

    private val client = OkHttpClient()

    private val api = ApiClient.retrofitService

    private val apiKey = "a4a83495ddmshf7e0965c9e681e9p14c029jsn3aaf07be0b5c"
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
                Log.e("TankAPI", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("TankAPI", "API Error (${it.code}): ${it.body?.string()}")
                        return
                    }

                    val responseBody = it.body?.string()
                    if (responseBody != null) {
                        Log.d("TankAPI", "RAW JSON:\n$responseBody")

                        try {
                            val parsed = Gson().fromJson(responseBody, MLBGameResponse::class.java)
                            val nowEpoch = System.currentTimeMillis() / 1000
                            val gameList = parsed.body.map { game ->

                                val inferredStatus = when {
                                    game.gameStatus == "In Progress" -> "In Progress"
                                    game.gameStatus == "Completed" -> "Completed"
                                    game.gameTime_epoch.toDoubleOrNull()?.let { it <= nowEpoch } == true -> "In Progress"
                                    else -> "Scheduled"
                                }

                                GameDetails(
                                    gameId = game.gameID,
                                    away = game.away,
                                    home = game.home,
                                    gameTime = game.gameTime,
                                    gameStatus = inferredStatus
                                )
                            }

                            _games.value = gameList
                            Log.d("TankAPI", "Loaded ${gameList.size} games.")
                        } catch (ex: Exception) {
                            Log.e("TankAPI", "Exception while parsing JSON: ${ex.message}")
                        }
                    } else {
                        Log.e("TankAPI", "Empty response body")
                    }
                }
            }
        })
    }

    fun fetchOdds(gameDate: String) {
        viewModelScope.launch {
            try {
                val response = api.getMLBBettingOdds(gameDate)
                if (response.isSuccessful) {
                    val gameOdds = response.body() ?: emptyList()
                    _odds.value = gameOdds
                    Log.d("TankAPI", "✅ Odds loaded: ${gameOdds.size} items")
                } else {
                    Log.e("TankAPI", "❌ Failed to load odds: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TankAPI", "🔥 Odds fetch failed", e)
            }
        }
    }

    fun loadFakeLeaderboard() {
        val fakeLeaderboard = listOf(
            LeaderboardEntry("Tannon", 5000),
            LeaderboardEntry("Alex", 4200),
            LeaderboardEntry("Jamie", 3900),
            LeaderboardEntry("Riley", 3400),
            LeaderboardEntry("Sam", 2800)
        ).sortedByDescending { it.score }

        _leaderboard.value = fakeLeaderboard
    }
}
