package edu.quinnipiac.ser210.finalproject.model

data class GameDetails(
    val gameId: String,
    val away: String,
    val home: String,
    val gameTime: String,
    val gameStatus: String,
    val gameDate: String // NEW: "Scheduled", "In Progress", "Completed", etc.
)
