package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.models.session.Session

data class SessionDetailState(
    val session: Session? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
