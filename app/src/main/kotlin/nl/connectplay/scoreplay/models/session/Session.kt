package nl.connectplay.scoreplay.models.session

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.game.Game

@Serializable
data class Session(
    val sessionId: String,
    val game: Game,
    val hostId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val endOfSessionPictureUrl: String?,
    val visibility: SessionVisibility
)