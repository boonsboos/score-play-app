package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchGameDto(
    val gameId: String,
    val title: String,
    val description: String?
)