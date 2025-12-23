package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.dto.ScoreDto
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.screens.Screens

@Serializable
@SerialName("highscore")
class HighscoreEvent(val game: Game, val score: ScoreDto, val podium: Int) : BaseEvent()