package nl.connectplay.scoreplay.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nl.connectplay.scoreplay.models.session.RoundScoreRow
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

@Dao
interface SessionScoreDao {
    /**
     * Insert a full round of scores in one transaction-like call (Room will batch the inserts).
     * Each RoomSessionScore row represents one player's score for a specific turn.
     */
    @Insert
    suspend fun insertAll(scores: List<RoomSessionScore>)

    /**
     * Returns the highest turn number already stored for the given session.
     * Used to compute the next turn index: (maxTurn ?: 0) + 1.
     */
    @Query("SELECT MAX(turn) FROM session_scores WHERE sessionId = :sessionId")
    suspend fun getMaxTurn(sessionId: Int): Int?

    /**
     * Observe the list of turns for the "rounds overview" screen.
     * Emits whenever session_scores changes for this session.
     */
    @Query("SELECT DISTINCT turn FROM session_scores WHERE sessionId = :sessionId ORDER BY turn ASC")
    fun observeTurns(sessionId: Int): Flow<List<Int>>

    /**
     * Observe all scores for one specific round/turn.
     * Joins scores with players to return a UI-friendly row containing player identity + score.
     *
     * Used by the round detail screen so it updates automatically when scores change.
     */
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

    /**
     * Fetch a single score row by its primary key.
     * Useful for edit flows or debugging.
     */
    @Query("SELECT * FROM session_scores WHERE id = :id")
    suspend fun getSessionScoreById(id: Int): RoomSessionScore

    /**
     * Fetch all scores for the active session.
     * Note: this query currently does not filter by sessionId; it assumes you only store one active session locally.
     * If you ever support multiple sessions, add `WHERE sessionId = :sessionId`.
     */
    @Query("SELECT * FROM session_scores ORDER BY turn ASC")
    suspend fun getScoresForSession(): List<RoomSessionScore>

    /**
     * Clears all locally stored scores (used when starting a new session).
     */
    @Query("DELETE FROM session_scores")
    suspend fun deleteAllScores()
}