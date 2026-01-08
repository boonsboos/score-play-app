package nl.connectplay.scoreplay.room.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import nl.connectplay.scoreplay.models.SessionVisibility

/**
 * Room entity representing the locally persisted "active session".
 *
 * The unique index on gameId helps prevent storing multiple sessions with different gameIds.
 */
@Entity(
    tableName = "sessions",
    indices = [Index(value = ["gameId"], unique = true)]
)
data class RoomSession(
    val userId: Int,
    val gameId: Int,
    val visibility: SessionVisibility,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)