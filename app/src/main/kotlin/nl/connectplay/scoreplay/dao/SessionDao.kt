package nl.connectplay.scoreplay.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import nl.connectplay.scoreplay.models.session.DbSession

@Dao
interface SessionDao {

    /**
     * Upsert means insert and or update
     */
    @Upsert
    suspend fun upsertSession(session: DbSession)

    @Delete
    suspend fun deleteSession(session: DbSession)


}