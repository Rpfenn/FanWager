package edu.quinnipiac.ser210.finalproject.model

data class PlayerProp(
    val playerID: String?,
    val propBets: PropBets
)

data class PropBets(
    val hits: Hits? = null,
    val bases: Bases? = null,
    val homeruns: Homeruns? = null,
    val sbs: Sbs? = null,
    val runs: Runs? = null,
    val rbis: Rbis? = null,
    val strikeouts: Strikeouts? = null,
    val er: EarnedRuns? = null
)

data class Hits(val one: String?, val two: String?)
data class Bases(val over: String?, val under: String?, val total: String?)
data class Homeruns(val one: String?)
data class Sbs(val one: String?)
data class Runs(val over: String?, val under: String?, val total: String?)
data class Rbis(val one: String?)
data class Strikeouts(val over: String?, val under: String?, val total: String?)
data class EarnedRuns(val over: String?, val under: String?, val total: String?)
