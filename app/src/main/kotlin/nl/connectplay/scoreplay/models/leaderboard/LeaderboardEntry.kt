package nl.connectplay.scoreplay.models.leaderboard

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(val playerName: String, val score: Double, val achievedAt: LocalDateTime)
