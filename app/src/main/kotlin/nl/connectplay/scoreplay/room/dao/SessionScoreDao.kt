package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

@Dao
interface SessionScoreDao {
    @Insert
    suspend fun insertAll(scores: List<RoomSessionScore>)

    @Query("SELECT MAX(turn) FROM session_scores WHERE sessionId = :sessionId")
    suspend fun getMaxTurn(sessionId: Int): Int?

    // Rounds overview
    @Query("SELECT DISTINCT turn FROM session_scores WHERE sessionId = :sessionId ORDER BY turn ASC")
    fun observeTurns(sessionId: Int): Flow<List<Int>>

    // Round detail (JOIN scores + players to show names)
    @Query("""
        SELECT 
            p.sessionPlayerId AS sessionPlayerId,
            p.guestName AS guestName,
            s.score AS score
        FROM session_scores s
        INNER JOIN session_players p ON p.sessionPlayerId = s.sessionPlayerId
        WHERE s.sessionId = :sessionId AND s.turn = :turn
        ORDER BY p.sessionPlayerId ASC
    """)
    fun observeRoundScores(sessionId: Int, turn: Int): Flow<List<RoundScoreRow>>

    @Query("SELECT * FROM session_scores WHERE id = :id")
    suspend fun getSessionScoreById(id: Int): RoomSessionScore
}

data class RoundScoreRow(
    val sessionPlayerId: Int,
    val guestName: String?,
    val score: Double
)