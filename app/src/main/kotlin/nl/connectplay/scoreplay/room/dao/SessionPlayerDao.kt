package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

@Dao
interface SessionPlayerDao {
    /**
     * Upsert means update if exists otherwise insert
     */
    @Upsert
    suspend fun upsertSessionPlayer(roomSessionPlayer: RoomSessionPlayer)

    @Query("SELECT * FROM session_players WHERE sessionPlayerId = :sessionPlayerId")
    suspend fun getSessionPlayerById(sessionPlayerId: Int): RoomSessionPlayer

    @Query("DELETE FROM session_players")
    suspend fun deleteAllPlayers()
}