package edu.quinnipiac.ser210.finalproject.model



data class TopPerformers(
    val away: TeamPerformers?,
    val home: TeamPerformers?
)

data class TeamPerformers(
    val Hitting: Map<String, StatDetail>?,
    val Fielding: Map<String, StatDetail>?,
    val Pitching: Map<String, StatDetail>?,
    val BaseRunning: Map<String, StatDetail>?
)

data class StatDetail(
    val playerID: List<String>,
    val total: String
)
