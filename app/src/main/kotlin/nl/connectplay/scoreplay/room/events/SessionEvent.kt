package nl.connectplay.scoreplay.room.events

import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.session.RoundScoreInput

/**
 * UI -> ViewModel events for the session flow.
 *
 * The screens dispatch these events; SessionViewModel handles them to:
 * - update in-memory UI state
 * - persist to Room
 * - trigger backend API calls (finish/upload)
 */
sealed interface SessionEvent {
    object StartNewSession: SessionEvent

    data class Initialize(val userId: Int): SessionEvent

    data class SetGame(val gameId: Int): SessionEvent

    data class SetVisibility(val visibility: SessionVisibility): SessionEvent

    data class UpdateVisibility(val visibility: SessionVisibility): SessionEvent

    object SaveSession: SessionEvent

    data class AddPlayer(val userId: Int, val guestName: String? = null): SessionEvent

    data class RemovePlayer(val userId: Int, val guestName: String?) : SessionEvent

    data class AddRound(val sessionId: Int, val gameId: Int, val scores: List<RoundScoreInput>) : SessionEvent

    object FinishSession: SessionEvent
}