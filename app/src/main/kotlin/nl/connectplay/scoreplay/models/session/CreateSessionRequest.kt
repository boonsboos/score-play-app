package nl.connectplay.scoreplay.models.session

data class CreateSessionRequest(
    val gameId: Int,
    val userId: Int,
    val visibility: Int
)
