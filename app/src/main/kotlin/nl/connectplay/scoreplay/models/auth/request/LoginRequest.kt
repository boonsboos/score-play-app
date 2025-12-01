package nl.connectplay.scoreplay.models.auth.request

import kotlinx.serialization.Serializable

// datamodel for LoginRequest (DTO)
@Serializable
data class LoginRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String
)
