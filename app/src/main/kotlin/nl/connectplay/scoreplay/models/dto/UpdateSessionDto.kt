package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSessionDto(
    val endTime: String? = null,
    val visibility: Int? = null
)