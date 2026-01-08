package nl.connectplay.scoreplay.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a player in the currently active local session.
 *
 * Identity rules in this app:
 * - The owner player is stored as (userId = logged-in userId, guestName = null).
 * - Guest players are stored as (userId = owner userId, guestName = "Guest name").
 * - Friend players are stored as (userId = friend's userId, guestName = friend's display name or null depending on your mapping).
 *
 * sessionPlayerId is a local-only identifier used by session_scores to reference players.
 */
@Entity(tableName = "session_players")
data class RoomSessionPlayer (
    val userId: Int,
    val guestName: String? = null,
    @PrimaryKey(autoGenerate = true)
    val sessionPlayerId: Int = 0
)