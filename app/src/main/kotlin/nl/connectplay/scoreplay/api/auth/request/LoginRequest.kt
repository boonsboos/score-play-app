package nl.connectplay.scoreplay.api.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String
)
