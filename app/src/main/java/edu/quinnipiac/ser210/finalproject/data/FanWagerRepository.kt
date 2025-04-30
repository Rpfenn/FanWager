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

    suspend fun getAnyUser(): User? {
        return db.userDao().getAnyUser()
    }

    suspend fun insertUser(user: User) {
        db.userDao().insertUser(user)
    }

    suspend fun getUnconcludedPredictions(gameId: String): List<Prediction> {
        return db.predictionDao().getUnconcludedPredictionsForGame(gameId)
    }

    suspend fun markPredictionAsResult(predictionId: Int, result: String) {
        db.predictionDao().markPredictionResult(predictionId, result)
    }

    suspend fun getUserById(id: Int): User? {
        return db.userDao().getUserById(id)
    }

    suspend fun updateUser(user: User) {
        db.userDao().updateUser(user)
    }

}