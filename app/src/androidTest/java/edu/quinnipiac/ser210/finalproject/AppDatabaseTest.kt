package edu.quinnipiac.ser210.finalproject

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import edu.quinnipiac.ser210.finalproject.data.AppDatabase
import edu.quinnipiac.ser210.finalproject.data.GameDao
import edu.quinnipiac.ser210.finalproject.data.Prediction
import edu.quinnipiac.ser210.finalproject.data.PredictionDao
import edu.quinnipiac.ser210.finalproject.data.User
import edu.quinnipiac.ser210.finalproject.data.UserDao
import edu.quinnipiac.ser210.finalproject.model.GameDetails
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var gameDao: GameDao
    private lateinit var userDao: UserDao
    private lateinit var predictionDao: PredictionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // for testing only
            .build()

        gameDao = db.gameDao()
        userDao = db.userDao()
        predictionDao = db.predictionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndReadGame() = runBlocking {
        val game = GameDetails(
            gameId = "12345",
            away = "Yankees",
            home = "Red Sox",
            gameTime = "7:00 PM",
            gameStatus = "Scheduled",
            gameDate = "4252025"
        )
        gameDao.insertGames(listOf(game))
        val result = gameDao.getAllGames()
        assert(result.isNotEmpty())
        assert(result[0].home == "Red Sox")
    }

    @Test
    fun insertAndReadUserPrediction() = runBlocking {
        val userId = userDao.insertUser(User(username = "alex", currency = 100)).toInt()
        val gameId = "g123"

        val prediction = Prediction(
            userOwnerId = userId,
            gameId = gameId,
            predictedWinner = "Yankees",
            bettingOdds = "+150",
            betAmount = 100
        )

        predictionDao.insertPrediction(prediction)
        val userPredictions = predictionDao.getPredictionsForUser(userId)
        assert(userPredictions.first().predictedWinner == "Yankees")
    }
}