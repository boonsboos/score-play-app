package nl.connectplay.scoreplay.models.session

data class RoundScoreRow(
    val sessionPlayerId: Int,
    val guestName: String?,
    val score: Double
)