package nl.connectplay.scoreplay.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.models.session.Session

@Dao
interface SessionDao {

    /**
     * Upsert means insert and or update
     */
    @Upsert
    suspend fun upsertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM session WHERE id = :id")
    fun getSessionById(id: String): Session

}