package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionDto(
    val gameId: Int,
    val userId: Int,
    val visibility: Int
)