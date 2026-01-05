package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

@Dao
interface SessionPlayerDao {
    /**
     * Insert or update a player entry for the active session.
     * Upsert = insert when missing, update when the row already exists.
     *
     * represents the current session's players only.
     */
    @Upsert
    suspend fun upsertSessionPlayer(roomSessionPlayer: RoomSessionPlayer)

    /**
     * Returns all players currently stored for the active session.
     */
    @Query("SELECT * FROM session_players")
    suspend fun getSessionPlayers(): List<RoomSessionPlayer>

    /**
     * Delete a specific player.
     *
     * Players are identified by:
     * - userId (always present)
     * - guestName (nullable)
     *
     * The NULL-safe comparison is important:
     * - If guestName is null, we must match rows where guestName IS NULL (owner player).
     * - If guestName is non-null, match by exact string equality.
     */
    @Query("""
        DELETE FROM session_players
        WHERE userId = :userId
          AND ((:guestName IS NULL AND guestName IS NULL) OR guestName = :guestName)
    """)
    suspend fun deleteSessionPlayer(userId: Int, guestName: String?)

    /**
     * Clear all players from local storage (used when starting a new session).
     */
    @Query("DELETE FROM session_players")
    suspend fun deleteAllPlayers()
}