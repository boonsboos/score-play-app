package nl.connectplay.scoreplay.room.events

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

sealed interface SessionEvent {
    data class Initialize(val userId: Int): SessionEvent

    object SaveSession: SessionEvent

    data class SetGame(val gameId: Int): SessionEvent

    data class SetVisibility(val visibility: SessionVisibility): SessionEvent

    object StartNewSession: SessionEvent
    
    data class AddPlayer(val userId: Int, val guestName: String? = null): SessionEvent

    data class RemovePlayer(val userId: Int, val guestName: String?) : SessionEvent

    data class DeleteSessionPlayer(val roomSessionPlayer: RoomSessionPlayer): SessionEvent

}