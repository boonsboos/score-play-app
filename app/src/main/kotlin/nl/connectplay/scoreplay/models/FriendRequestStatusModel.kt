package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
enum class FriendRequestStatus {
    PENDING,
    INCOMING,
    SENT
}