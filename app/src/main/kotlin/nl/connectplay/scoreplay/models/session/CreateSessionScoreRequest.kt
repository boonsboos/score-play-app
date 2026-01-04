package nl.connectplay.scoreplay.models.session

data class CreateSessionScoreRequest(
    val score: Double,
    val turn: Int,
    val sessionPlayer: SessionPlayerDto,
)