package edu.quinnipiac.ser210.finalproject.model

data class MLBGameResponse(
    val statusCode: Int,
    val body: List<Game> // ← NOT GameDetails
)
