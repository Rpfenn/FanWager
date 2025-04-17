package edu.quinnipiac.ser210.finalproject.model

data class Game(
    val away: String,
    val home: String,
    val teamIDAway: String,
    val teamIDHome: String,
    val gameTime: String,
    val lineScore: LineScore
)

data class LineScore(
    val away: TeamScore,
    val home: TeamScore
)

data class TeamScore(
    val R: String
)
