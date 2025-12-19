package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

enum class SessionStatus {
    DRAFT,
    SAVED,
    ERROR
}


data class SessionState(
    val roomSession: RoomSession? = null,
    val gameId: Int? = null,
    val userId: Int? = null,
    val status: SessionStatus = SessionStatus.DRAFT,
    val sessionPlayers: List<RoomSessionPlayer> = emptyList(),
    val visibility: SessionVisibility = SessionVisibility.ANONYMISED,
)