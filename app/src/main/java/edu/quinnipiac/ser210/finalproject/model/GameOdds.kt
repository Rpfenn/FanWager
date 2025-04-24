package edu.quinnipiac.ser210.finalproject.model

data class GameOdds(
    val gameID: String,
    val last_updated_e_time: String?,
    val teamIDAway: String?,
    val teamIDHome: String?,
    val homeTeam: String?,
    val awayTeam: String?,
    val gameDate: String,
    val playerProps: List<PlayerProp> = emptyList(),
    val sportsBooks: List<SportsBookOdds> = emptyList()
)
