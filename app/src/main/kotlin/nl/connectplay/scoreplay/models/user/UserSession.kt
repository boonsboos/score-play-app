package nl.connectplay.scoreplay.models.user

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.game.Game

@Serializable
data class UserSession(
    @SerialName("sessionId") val id: String,
    val game: Game,
    val hostId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val endOfSessionPictureUrl: String?,
    val visibility: String
)