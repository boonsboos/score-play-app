package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.dao.SessionPlayerDao
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

sealed class SessionStatus {
    object Draft : SessionStatus()
    data class Saved(val sessionId: Int) : SessionStatus()
}


data class SessionState(
    val roomSession: RoomSession? = null,
    val gameId: Int? = null,
    val userId: Int? = null,
    val status: SessionStatus = SessionStatus.Draft,
    val sessionPlayers: List<RoomSessionPlayer> = emptyList(),
    val visibility: SessionVisibility = SessionVisibility.ANONYMISED,
)
