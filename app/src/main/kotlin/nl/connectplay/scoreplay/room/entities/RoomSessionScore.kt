package nl.connectplay.scoreplay.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "session_scores",
    foreignKeys = [
        ForeignKey(
            entity = RoomSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomSession::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoomSessionPlayer::class,
            parentColumns = ["sessionPlayerId"],
            childColumns = ["sessionPlayerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sessionId"),
        Index("gameId"),
        Index("sessionPlayerId")
    ]
)
data class RoomSessionScore(
    val sessionId: Int,
    val sessionPlayerId: Int,
    val gameId: Int,
    val score: Double,
    val turn: Int = 0,
    val achievedOn: Instant = Instant.now(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
