package nl.connectplay.scoreplay.models.session

import androidx.room.Entity
import androidx.room.PrimaryKey
import nl.connectplay.scoreplay.models.SessionVisibility

@Entity
data class Session(
    val userId: Int,
    val gameId: Int,
    val visibility: SessionVisibility,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
