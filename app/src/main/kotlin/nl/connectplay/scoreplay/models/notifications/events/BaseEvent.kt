package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Serializable
sealed class BaseEvent(val created: Instant = Clock.System.now())