package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.room.entities.RoomSession

@Dao
interface SessionDao {

    /**
     * Upsert means update if exist otherwise insert
     */
    @Upsert
    suspend fun upsertSession(roomSession: RoomSession)

    @Delete
    suspend fun deleteSession(roomSession: RoomSession)

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getSessionById(id: Int): RoomSession

}