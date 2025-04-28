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
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import edu.quinnipiac.ser210.finalproject.model.Game
import edu.quinnipiac.ser210.finalproject.model.GameOdds
import edu.quinnipiac.ser210.finalproject.model.LeaderboardEntry
import edu.quinnipiac.ser210.finalproject.model.SportsBookOdds
import edu.quinnipiac.ser210.finalproject.network.RetrofitInstance.api
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.viewModelScope
import edu.quinnipiac.ser210.finalproject.api.ApiClient
import edu.quinnipiac.ser210.finalproject.data.FanWagerRepository
import edu.quinnipiac.ser210.finalproject.data.Prediction
import edu.quinnipiac.ser210.finalproject.ui.theme.ColorSchemeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanWagerViewModel(private val repository: FanWagerRepository) : ViewModel() {

    private val _theme = MutableStateFlow(ColorSchemeType.LIGHT)
    val theme: StateFlow<ColorSchemeType> = _theme

    private val _games = MutableStateFlow<List<GameDetails>>(emptyList())
    val games: StateFlow<List<GameDetails>> = _games

    private val _odds = MutableStateFlow<GameOdds?>(null)
    val odds: StateFlow<GameOdds?> = _odds

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
                                    gameStatus = inferredStatus,
                                    gameDate = today
                                )
                            }

                            _games.value = gameList

                            viewModelScope.launch(Dispatchers.IO) {
                                repository.insertGames(gameList)
                            }
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

    fun placeBet(prediction: Prediction){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPrediction(prediction)
        }
    }

    fun fetchOdds(gameId: String, gameDate: String) {
        viewModelScope.launch {
            try {
                Log.d("TankAPI", "📡 Fetching betting odds for $gameDate ($gameId)")
                Log.d(
                    "TankAPI",
                    "Calling endpoint with gameDate=$gameDate, key=$apiKey, tank01-mlb-live-in-game-real-time-statistics.p.rapidapi.com"
                )
                val response = api.getBettingOdds(
                    gameDate
                )
                if (response.isSuccessful) {
                    val allOdds = response.body()?.body ?: emptyList()
                    val matchingGame = allOdds.find { it.gameID == gameId }
                    if (matchingGame != null) {
                        _odds.value = matchingGame
                        Log.d(
                            "TankAPI",
                            "✅ Found odds for $gameId with ${matchingGame.sportsBooks.size} books"
                        )
                    } else {
                        Log.w("TankAPI", "⚠️ No odds found for $gameId")
                    }
                } else {
                    Log.e("TankAPI", "❌ API Error (${response.code()}): ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TankAPI", "❌ Exception while fetching odds", e)
            }
        }
    }

    fun fetchDailyScoreboardLive() {
        viewModelScope.launch {
            try {
                val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                val response = api.getGames(
                    gameDate = today
                )

                if (response.isSuccessful) {
                    val body = response.body()?.body  // 🚨 Note: accessing .body inside MLBGameResponse
                    if (body != null) {
                        val nowEpoch = System.currentTimeMillis() / 1000
                        val gameList = body.map { (gameId, game) ->

                            val inferredStatus = when {
                                game.gameStatus == "In Progress" -> "In Progress"
                                game.gameStatus == "Completed" -> "Completed"
                                game.gameTime_epoch.toDoubleOrNull()?.let { it <= nowEpoch } == true -> "In Progress"
                                else -> "Scheduled"
                            }

                            val awayScore = game.lineScore?.away?.R
                            val homeScore = game.lineScore?.home?.R

                            GameDetails(
                                gameId = gameId,
                                away = game.away,
                                home = game.home,
                                gameTime = game.gameTime,
                                gameStatus = inferredStatus,
                                gameDate = today,
                                awayScore = awayScore,
                                homeScore = homeScore
                            )
                        }

                        _games.value = gameList

                        viewModelScope.launch(Dispatchers.IO) {
                            repository.insertGames(gameList)
                        }

                        Log.d("TankAPI", "✅ Loaded ${gameList.size} daily live games with scores.")
                    } else {
                        Log.e("TankAPI", "❌ Empty body in Daily Scoreboard Live response")
                    }
                } else {
                    Log.e("TankAPI", "❌ API Error (${response.code()}): ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TankAPI", "❌ Exception while fetching daily scoreboard live", e)
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

    fun setTheme(option: ColorSchemeType) {
        _theme.value = option
    }
}
