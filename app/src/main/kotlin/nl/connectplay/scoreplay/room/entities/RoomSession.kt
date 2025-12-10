package nl.connectplay.scoreplay.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import nl.connectplay.scoreplay.models.SessionVisibility

@Entity(tableName = "sessions")
data class RoomSession(
    val userId: Int,
    val gameId: Int,
    val visibility: SessionVisibility,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)