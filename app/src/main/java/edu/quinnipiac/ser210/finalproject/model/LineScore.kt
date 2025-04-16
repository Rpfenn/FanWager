



data class LineScore(
    val away: TeamScore,
    val home: TeamScore
)

data class TeamScore(
    val team: String,
    val R: String,
    val H: String,
    val E: String,
    val scoresByInning: Map<String, String>
)
