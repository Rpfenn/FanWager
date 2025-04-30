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
import edu.quinnipiac.ser210.finalproject.api.ApiClient
import edu.quinnipiac.ser210.finalproject.data.FanWagerRepository
import edu.quinnipiac.ser210.finalproject.data.GlobalVariables
import edu.quinnipiac.ser210.finalproject.data.Prediction
import edu.quinnipiac.ser210.finalproject.data.User
import edu.quinnipiac.ser210.finalproject.ui.theme.ColorSchemeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FanWagerViewModel(private val repository: FanWagerRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _currency = MutableStateFlow(0)
    val currency: StateFlow<Int> = _currency

    private val _theme = MutableStateFlow(ColorSchemeType.LIGHT)
    val theme: StateFlow<ColorSchemeType> = _theme

    private val _games = MutableStateFlow<List<GameDetails>>(emptyList())
    val games: StateFlow<List<GameDetails>> = _games

    private val _odds = MutableStateFlow<GameOdds?>(null)
    val odds: StateFlow<GameOdds?> = _odds

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard

    private val _betPlacedSuccessfully = MutableStateFlow(false)
    val betPlacedSuccessfully: StateFlow<Boolean> = _betPlacedSuccessfully

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
                        } catch (ex: Exception) {
                            Log.e("TankAPI", "Exception while parsing JSON: ${ex.message}")
                        }
                    }
                }
            }
        })
    }

    fun placeBet(prediction: Prediction) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserById(prediction.userOwnerId)
            if (user != null && user.currency >= prediction.betAmount) {
                val updatedUser = user.copy(currency = user.currency - prediction.betAmount)
                repository.updateUser(updatedUser)

                if (user.userId == GlobalVariables.currentUser) {
                    _currency.value = updatedUser.currency
                }

                repository.insertPrediction(prediction)
                _betPlacedSuccessfully.value = true

                loadLeaderboardFromDatabase() // ✅ update leaderboard
            } else {
                _errorMessage.value = "You do not have enough currency to place this bet."
            }
        }
    }

    fun resetBetPlacedFlag() {
        _betPlacedSuccessfully.value = false
    }

    fun fetchOdds(gameId: String, gameDate: String) {
        viewModelScope.launch {
            try {
                val response = api.getBettingOdds(gameDate)
                if (response.isSuccessful) {
                    val allOdds = response.body()?.body ?: emptyList()
                    val matchingGame = allOdds.find { it.gameID == gameId }
                    if (matchingGame != null) {
                        _odds.value = matchingGame
                    }
                }
            } catch (e: Exception) {
                Log.e("TankAPI", "Exception while fetching odds", e)
            }
        }
    }

    fun fetchDailyScoreboardLive() {
        viewModelScope.launch {
            try {
                val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                val response = api.getGames(gameDate = today)

                if (response.isSuccessful) {
                    val body = response.body()?.body
                    if (body != null) {
                        val nowEpoch = System.currentTimeMillis() / 1000
                        val gameList = body.map { (gameId, game) ->
                            val status = when {
                                game.gameStatus == "In Progress" -> "In Progress"
                                game.gameStatus == "Completed" -> "Completed"
                                game.gameTime_epoch.toDoubleOrNull()?.let { it <= nowEpoch } == true -> "In Progress"
                                else -> "Scheduled"
                            }

                            GameDetails(
                                gameId = gameId,
                                away = game.away,
                                home = game.home,
                                gameTime = game.gameTime,
                                gameStatus = status,
                                gameDate = today,
                                awayScore = game.lineScore?.away?.R,
                                homeScore = game.lineScore?.home?.R
                            )
                        }

                        _games.value = gameList

                        validateCompletedPredictions()

                        viewModelScope.launch(Dispatchers.IO) {
                            repository.insertGames(gameList)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TankAPI", "Exception in fetchDailyScoreboardLive", e)
            }
        }
    }

    fun loadLeaderboardFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = repository.getAllUsers()
            val realEntries = users.map {
                LeaderboardEntry(username = it.username, score = it.currency)
            }

            val fakeUsers = listOf(
                LeaderboardEntry("Alex", (0..2000).random()),
                LeaderboardEntry("Jordan", (0..2000).random()),
                LeaderboardEntry("Taylor", (0..2000).random()),
                LeaderboardEntry("Casey", (0..2000).random()),
                LeaderboardEntry("Drew", (0..2000).random()),
                LeaderboardEntry("Jack", (0..2000).random()),
                LeaderboardEntry("Zach", (0..2000).random()),
                LeaderboardEntry("Aidan", (0..2000).random()),
                LeaderboardEntry("Conner", (0..2000).random())
            )

            val allEntries = (realEntries + fakeUsers).sortedByDescending { it.score }
            _leaderboard.value = allEntries
        }
    }

    fun validateCompletedPredictions() {
        viewModelScope.launch(Dispatchers.IO) {
            val completedGames = _games.value.filter {
                it.gameStatus == "Completed" && it.homeScore != null && it.awayScore != null
            }

            for (game in completedGames) {
                val winner = when {
                    game.homeScore!!.toInt() > game.awayScore!!.toInt() -> game.home
                    game.homeScore.toInt() < game.awayScore!!.toInt() -> game.away
                    else -> "TIE"
                }

                val predictions = repository.getUnconcludedPredictions(game.gameId)
                for (prediction in predictions) {
                    val user = repository.getUserById(prediction.userOwnerId) ?: continue

                    val correct = when (prediction.betType) {
                        "ML" -> prediction.predictedWinner == winner
                        "RL" -> {
                            val predictedScore = if (prediction.predictedWinner == game.home) game.homeScore!!.toDouble() else game.awayScore!!.toDouble()
                            val opponentScore = if (prediction.predictedWinner == game.home) game.awayScore!!.toDouble() else game.homeScore!!.toDouble()
                            val spread = prediction.line.toDoubleOrNull() ?: 0.0
                            predictedScore > opponentScore + -spread
                        }
                        "TO" -> {
                            val total = game.homeScore!!.toDouble() + game.awayScore!!.toDouble()
                            val target = prediction.line.toDoubleOrNull() ?: 0.0
                            if (prediction.predictedWinner == "Over") total > target else total < target
                        }
                        else -> false
                    }

                    val winnings = if (correct) {
                        calculateWinnings(prediction.betAmount, prediction.bettingOdds) + prediction.betAmount
                    } else 0

                    val newCurrency = user.currency + winnings
                    val updatedUser = user.copy(currency = newCurrency)
                    repository.updateUser(updatedUser)

                    if (user.userId == GlobalVariables.currentUser) {
                        delay(200)
                        val freshUser = repository.getUserById(user.userId)
                        withContext(Dispatchers.Main) {
                            _currency.value = freshUser?.currency ?: updatedUser.currency
                        }
                    }

                    repository.markPredictionAsResult(prediction.predictionId, if (correct) "win" else "loss")
                }
            }

            loadLeaderboardFromDatabase() // ✅ refresh leaderboard after scoring
        }
    }

    fun calculateWinnings(betAmount: Int, odds: String): Int {
        return try {
            val value = odds.replace(",", "").trim().toInt()
            if (value > 0) (betAmount * value / 100.0).toInt()
            else (betAmount * 100 / -value.toDouble()).toInt()
        } catch (e: Exception) {
            Log.e("BettingCalc", "Invalid odds format: $odds", e)
            0
        }
    }

    fun loadUserCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getAnyUser()
            if (user != null) {
                _currency.value = user.currency
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setTheme(option: ColorSchemeType) {
        _theme.value = option
    }

    fun createDefaultUserIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            val existingUser = repository.getAnyUser()
            if (existingUser == null) {
                repository.insertUser(User(username = "You", currency = 1000))
            }
        }
    }

    fun renameDefaultUserToYou() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getAnyUser()
            if (user != null && user.username == "DefaultUser") {
                val updated = user.copy(username = "You")
                repository.updateUser(updated)
            }
        }
    }
}
