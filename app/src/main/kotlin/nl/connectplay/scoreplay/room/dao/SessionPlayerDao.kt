package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

@Dao
interface SessionPlayerDao {
    /**
     * Upsert means update if exists otherwise insert
     */
    @Upsert
    suspend fun upsertSessionPlayer(roomSessionPlayer: RoomSessionPlayer)

    @Query("SELECT * FROM session_players")
    suspend fun getSessionPlayers(): List<RoomSessionPlayer>

    @Query("""
        DELETE FROM session_players
        WHERE userId = :userId
          AND ((:guestName IS NULL AND guestName IS NULL) OR guestName = :guestName)
    """)
    suspend fun deleteSessionPlayer(userId: Int, guestName: String?)

    @Query("DELETE FROM session_players")
    suspend fun deleteAllPlayers()
}