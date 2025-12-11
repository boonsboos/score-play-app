package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
data class FriendDto(
    val user: UserDto,
    val status: String
)
