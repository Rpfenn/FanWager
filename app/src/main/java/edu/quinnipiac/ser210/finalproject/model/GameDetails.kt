package edu.quinnipiac.ser210.finalproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameDetails(
    @PrimaryKey val gameId: String,
    val away: String,
    val home: String,
    val gameTime: String,
    val gameStatus: String,
    val gameDate: String // NEW: "Scheduled", "In Progress", "Completed", etc.
)
