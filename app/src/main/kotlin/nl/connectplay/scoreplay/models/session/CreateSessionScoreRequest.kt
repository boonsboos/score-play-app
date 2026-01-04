package nl.connectplay.scoreplay.models.session

import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.dto.score.SessionPlayerDto

@Serializable
data class CreateSessionScoreRequest(
    val score: Double,
    val turn: Int,
    val sessionPlayer: SessionPlayerDto,
)