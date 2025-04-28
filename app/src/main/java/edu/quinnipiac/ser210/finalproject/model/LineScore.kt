



data class LineScore(
    val away: TeamLineScore,
    val home: TeamLineScore
)

data class TeamLineScore(
    val team: String,
    val R: String,
    val H: String,
    val E: String,
    val scoresByInning: Map<String, String>
)
