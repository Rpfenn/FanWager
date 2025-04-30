package edu.quinnipiac.ser210.finalproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PredictionDao {
    @Insert
    suspend fun insertPrediction(prediction: Prediction): Long

    @Query("SELECT * FROM predictions WHERE userOwnerId = :userId")
    suspend fun getPredictionsForUser(userId: Int): List<Prediction>

    @Query("SELECT * FROM predictions WHERE gameId = :gameId AND concluded = 0")
    suspend fun getUnconcludedPredictionsForGame(gameId: String): List<Prediction>

    @Query("UPDATE predictions SET concluded = 1, result = :result WHERE predictionId = :predictionId")
    suspend fun markPredictionResult(predictionId: Int, result: String)

    @Query("DELETE FROM predictions WHERE result IN ('win', 'loss')")
    suspend fun deleteCompletedPredictions()

}