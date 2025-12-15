package nl.connectplay.scoreplay.room.events

import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

sealed interface SessionPlayerEvent {


    object ShowSetup: SessionPlayerEvent
    object ShowScores: SessionPlayerEvent
}