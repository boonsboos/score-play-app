package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.room.entities.RoomSession

@Dao
interface SessionDao {
    /**
     * Upsert means update if exists otherwise insert
     */
    @Upsert
    suspend fun upsertSession(roomSession: RoomSession)

    @Query("DELETE FROM sessions")
    suspend fun deleteSession()

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionById(id: Int): RoomSession
}