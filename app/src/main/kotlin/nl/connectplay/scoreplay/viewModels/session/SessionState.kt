package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.session.Session
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

enum class SessionStatus {
    DRAFT,
    SAVED,
    ERROR
}

data class SessionState(
    val roomSession: RoomSession? = null,
    val sessionPlayers: List<RoomSessionPlayer> = emptyList(),
    val gameId: Int? = null,
    val scores: List<RoomSessionScore> = emptyList(),
    val turns: List<Int> = emptyList(),
    val session: Session? = null,
    val sessionId: String? = null,
    val userId: Int? = null,
    val status: SessionStatus = SessionStatus.DRAFT,
    val visibility: SessionVisibility = SessionVisibility.ANONYMISED,
)
