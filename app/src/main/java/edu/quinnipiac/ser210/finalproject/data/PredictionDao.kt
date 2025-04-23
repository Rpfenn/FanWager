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



}