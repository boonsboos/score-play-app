package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.dto.ScoreDto

@Serializable
@SerialName("highscore")
class HighscoreEvent(val gameId: Int, val score: ScoreDto, val podium: Int) : BaseEvent()