package nl.connectplay.scoreplay.models.auth.request

import kotlinx.serialization.Serializable

// models for LoginRequest
@Serializable
data class LoginRequest(
    val username: String? = null,
    val email: String? = null,
    val password: String
)
