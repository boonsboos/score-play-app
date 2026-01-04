package nl.connectplay.scoreplay.viewModels.session

import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

data class WinnerResult(
    val player: RoomSessionPlayer,
    val totalScore: Double
)

/**
 * Winner = player with the highest total score (sum across all rounds).
 * If tie (within epsilon), picks the lowest sessionPlayerId.
 */
fun winnerHighestTotal(
    players: List<RoomSessionPlayer>,
    scores: List<RoomSessionScore>,
): WinnerResult? {
    val playerById = players.associateBy { it.sessionPlayerId }

    val totals = mutableMapOf<Int, Double>()
    for (s in scores) {
        totals[s.sessionPlayerId] = (totals[s.sessionPlayerId] ?: 0.0) + s.score
    }

    val winnerEntry = totals.entries
        .sortedWith(
            compareByDescending<Map.Entry<Int, Double>> { it.value }
                .thenBy { it.key }
        )
        .firstOrNull() ?: return null

    val winnerPlayer = playerById[winnerEntry.key] ?: return null
    return WinnerResult(player = winnerPlayer, totalScore = winnerEntry.value)
}

/**
 * Winner = player with the lowest total score (sum across all rounds).
 * If tie (within epsilon), picks the lowest sessionPlayerId.
 */
fun winnerLowestTotal(
    players: List<RoomSessionPlayer>,
    scores: List<RoomSessionScore>,
): WinnerResult? {
    val playerById = players.associateBy { it.sessionPlayerId }

    val totals = mutableMapOf<Int, Double>()
    for (s in scores) {
        totals[s.sessionPlayerId] = (totals[s.sessionPlayerId] ?: 0.0) + s.score
    }

    val winnerEntry = totals.entries
        .sortedWith(
            compareBy<Map.Entry<Int, Double>> { it.value }
                .thenBy { it.key }
        )
        .firstOrNull() ?: return null

    val winnerPlayer = playerById[winnerEntry.key] ?: return null
    return WinnerResult(player = winnerPlayer, totalScore = winnerEntry.value)
}