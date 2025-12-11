package nl.connectplay.scoreplay.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FriendshipStatus {
    @SerialName("rejected")
    REJECTED,

    @SerialName("pending")
    PENDING,

    @SerialName("accepted")
    ACCEPTED,

    @SerialName("friends")
    FRIENDS
}
