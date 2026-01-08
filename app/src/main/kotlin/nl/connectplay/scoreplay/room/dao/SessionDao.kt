package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.entities.RoomSession

/**
 * DAO (Data Access Object):
 * A small interface that defines all database operations for a specific entity/table.
 * Room generates the concrete implementation at compile time based on these annotations.
 */
@Dao
interface SessionDao {
    /**
     * Insert or update the active session.
     * Upsert = insert when no row exists, otherwise update the existing row.
     *
     * Note: this app stores only one "active session" locally, so the table is treated as single-row storage.
     */
    @Upsert
    suspend fun upsertSession(roomSession: RoomSession)

    /**
     * Update the session visibility for the active session.
     * Because this query has no WHERE clause, it updates all rows (fine for a single-row table).
     */
    @Query("UPDATE sessions SET visibility = :visibility")
    suspend fun updateVisibility(visibility: SessionVisibility)

    /**
     * Fetch the active session.
     * Assumes exactly one row exists; will throw if the table is empty.
     */
    @Query("SELECT * FROM sessions")
    suspend fun getSession(): RoomSession

    /**
     * Clear the active session from local database.
     */
    @Query("DELETE FROM sessions")
    suspend fun deleteSession()
}