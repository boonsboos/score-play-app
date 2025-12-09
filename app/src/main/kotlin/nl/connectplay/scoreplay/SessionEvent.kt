package nl.connectplay.scoreplay

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.session.Session

sealed interface SessionEvent {
    object SaveSession: SessionEvent

    data class SetGame(val gameId: String): SessionEvent
    data class SetVisibility(val visibility: SessionVisibility): SessionEvent
    data class DeleteSession(val session: Session): SessionEvent

    object showSetup: SessionEvent
    object showScores: SessionEvent
}