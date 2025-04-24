package edu.quinnipiac.ser210.finalproject.model

data class OddsResponse(
    val statusCode: Int,
    val body: List<GameOdds>
)
