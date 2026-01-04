package nl.connectplay.scoreplay.models.session

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    val gameId: Int,
    val userId: Int,
    val visibility: Int
)
