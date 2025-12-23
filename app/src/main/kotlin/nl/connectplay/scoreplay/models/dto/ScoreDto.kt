package nl.connectplay.scoreplay.models.dto

import nl.connectplay.scoreplay.utilities.UUIDSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.dto.score.SessionPlayerDto
import java.util.*

@Serializable
data class ScoreDto(
    @Serializable(with = UUIDSerializer::class) val scoreId: UUID,
    val score: Double,
    val turn: Int,
    val achievedOn: LocalDateTime,
    val sessionPlayer: SessionPlayerDto
)