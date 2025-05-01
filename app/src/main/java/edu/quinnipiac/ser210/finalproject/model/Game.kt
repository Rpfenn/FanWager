package edu.quinnipiac.ser210.finalproject.model

import LineScore

data class Game(
    val gameID: String,
    val away: String,
    val home: String,
    val teamIDAway: String,
    val teamIDHome: String,
    val gameTime: String,
    val gameDate: String,
    val gameTime_epoch: String,
    val gameStatus: String?,
    val lineScore: LineScore?
)

