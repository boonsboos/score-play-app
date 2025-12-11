package nl.connectplay.scoreplay.room.events

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.entities.RoomSession

sealed interface SessionEvent {
    object SaveSession: SessionEvent

    data class SetUser(val userId: Int): SessionEvent
    data class SetGame(val gameId: Int): SessionEvent
    data class SetVisibility(val visibility: SessionVisibility): SessionEvent
    data class DeleteSession(val roomSession: RoomSession): SessionEvent

    object ShowSetup: SessionEvent
    object ShowScores: SessionEvent
}