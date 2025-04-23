package edu.quinnipiac.ser210.finalproject.model

data class SportsBookOdds(
    val sportsBook: String,
    val odds: OddsDetails
)

data class OddsDetails(
    val awayTeamMLOdds: String?,
    val homeTeamMLOdds: String?,
    val awayTeamRunLine: String?,
    val homeTeamRunLine: String?,
    val awayTeamRunLineOdds: String?,
    val homeTeamRunLineOdds: String?,
    val totalOver: String?,
    val totalUnder: String?,
    val totalOverOdds: String?,
    val totalUnderOdds: String?
)

data class GameOdds(
    val gameID: String,
    val gameDate: String,
    val homeTeam: String,
    val awayTeam: String,
    val sportsBooks: List<SportsBookOdds>
)
