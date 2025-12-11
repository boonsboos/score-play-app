package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String,
    val profilePicture: String? = null
)