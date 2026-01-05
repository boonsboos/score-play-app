package nl.connectplay.scoreplay.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing one score entry:
 * one player's score for one specific turn/round within the active session.
 *
 * A single "round" is stored as multiple rows:
 * - one row per player (same sessionId + same turn, different sessionPlayerId).
 */
@Entity(
    tableName = "session_scores",
    foreignKeys = [
        /**
         * Link scores to the local session row.
         * When the session is deleted, all scores should be deleted as well.
         */
        ForeignKey(
            entity = RoomSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        /**
         * Link scores to the selected game.
         * Note: gameId is stored on RoomSession (not a separate Game entity in Room),
         * so this is effectively tying scores to the session's chosen gameId.
         * If you ever allow changing gameId mid-session or store multiple sessions,
         * consider whether this FK is still needed.
         */
        ForeignKey(
            entity = RoomSession::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        /**
         * Link scores to the local player row (sessionPlayerId).
         * If a player is removed, their scores are removed as well.
         */
        ForeignKey(
            entity = RoomSessionPlayer::class,
            parentColumns = ["sessionPlayerId"],
            childColumns = ["sessionPlayerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId"),
        Index("gameId"),
        Index("sessionPlayerId")
    ]
)
data class RoomSessionScore(
    val sessionId: Int,
    val sessionPlayerId: Int,
    val gameId: Int,
    val score: Double,
    val turn: Int = 0,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
