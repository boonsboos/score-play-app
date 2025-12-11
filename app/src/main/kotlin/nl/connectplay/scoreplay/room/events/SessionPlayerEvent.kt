package nl.connectplay.scoreplay.room.events

import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

sealed interface SessionPlayerEvent {
    object SaveSessionPlayer: SessionPlayerEvent

    data class SetUser(val userId: Int): SessionPlayerEvent
    data class DeleteSessionPlayer(val roomSessionPlayer: RoomSessionPlayer): SessionPlayerEvent

    object ShowSetup: SessionPlayerEvent
    object ShowScores: SessionPlayerEvent
}