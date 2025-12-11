package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.entities.RoomSession

data class SessionState(
    val roomSession: RoomSession? = null,
    val gameId: Int? = null,
    val userId: Int? = null,
    val visibility: SessionVisibility = SessionVisibility.ANONYMISED,
    val isOnSetup: Boolean = true,
)
