package nl.connectplay.scoreplay.room.dao

import androidx.room.Query
import androidx.room.Upsert
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

interface SessionScoreDao {
    @Upsert
    suspend fun upsertScore(roomSessionScore: RoomSessionScore)

    @Query("SELECT * FROM session_scores WHERE id = :id")
    suspend fun getSessionScoreById(id: Int): RoomSessionScore
}