package edu.quinnipiac.ser210.finalproject.model

import LineScore

data class Game(
    val gameID: String,              // ← NEW: unique ID from API
    val away: String,
    val home: String,
    val teamIDAway: String,
    val teamIDHome: String,
    val gameTime: String,
    val gameDate: String,            // ← NEW: available in API
    val gameTime_epoch: String,      // ← NEW: could be useful later
    val gameStatus: String?,         // ← NEW: this is how we know if it's "Live", "Completed", etc.
    val lineScore: LineScore?        // ← Nullable in case the game hasn't started yet
)

