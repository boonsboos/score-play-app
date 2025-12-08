package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchUserDto(
    val userId: String,
    val username: String,
    val pictureUrl: String? = null
)