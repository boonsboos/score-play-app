package nl.connectplay.scoreplay.models.auth.register

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val username: String,
    val password: String
)