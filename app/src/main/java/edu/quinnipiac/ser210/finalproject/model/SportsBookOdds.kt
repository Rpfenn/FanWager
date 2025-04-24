package edu.quinnipiac.ser210.finalproject.model

data class SportsBookOdds(
    val sportsBook: String,
    val odds: OddsDetail
)

data class OddsDetail(
    val totalUnder: String?,
    val totalOver: String?,
    val totalUnderOdds: String?,
    val totalOverOdds: String?,
    val awayTeamRunLine: String?,
    val awayTeamRunLineOdds: String?,
    val awayTeamMLOdds: String?,
    val homeTeamRunLine: String?,
    val homeTeamRunLineOdds: String?,
    val homeTeamMLOdds: String?
)
