package nl.connectplay.scoreplay.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_players")
data class RoomSessionPlayer (
    val userId: Int,
    val guestName: String? = null,
    @PrimaryKey(autoGenerate = true)
    val sessionPlayerId: Int
)