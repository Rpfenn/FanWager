package edu.quinnipiac.ser210.finalproject.data

import edu.quinnipiac.ser210.finalproject.model.GameDetails

class FanWagerRepository(private val db: AppDatabase) {

    suspend fun insertPrediction(prediction: Prediction) {
        db.predictionDao().insertPrediction(prediction)
    }

    suspend fun insertGames(games: List<GameDetails>) {
        db.gameDao().insertGames(games)
    }


    suspend fun getAllGames(): List<GameDetails> {
        return db.gameDao().getAllGames()
    }



}