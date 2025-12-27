package nl.connectplay.scoreplay.utilities

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
    Utility extension functions for DateTime formatting
 */

/**
 * Formats an Instant to dd-mm-yyyy HH:MM format
 */
@OptIn(ExperimentalTime::class)
fun Instant.formatted() = this.toLocalDateTime(TimeZone.currentSystemDefault())
    .format(
        LocalDateTime.Format {
            day()
            char('-')
            monthNumber()
            char('-')
            year()
            char(' ')
            hour()
            char(':')
            minute()
        }
    )

/**
 * Formats a LocalDateTime to dd-mm-yyyy HH:MM format
 */
fun LocalDateTime.formatted() = this.format(
    LocalDateTime.Format {
        day()
        char('-')
        monthNumber()
        char('-')
        year()
        char(' ')
        hour()
        char(':')
        minute()
    }
)

fun LocalDateTime.formattedDate() = this.format(
LocalDateTime.Format {
        day()
        char('-')
        monthNumber()
        char('-')
        year()
    }
)