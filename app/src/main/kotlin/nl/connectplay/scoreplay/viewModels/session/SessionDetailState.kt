package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.models.dto.ScoreDto
import nl.connectplay.scoreplay.models.session.Session

data class SessionDetailState(
    val session: Session? = null,
    val scores: List<ScoreDto>? = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
