package nl.connectplay.scoreplay.room

import androidx.room.Database
import androidx.room.RoomDatabase
import nl.connectplay.scoreplay.room.dao.SessionDao
import nl.connectplay.scoreplay.room.dao.SessionPlayerDao
import nl.connectplay.scoreplay.room.dao.SessionScoreDao
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

/**
 * Room database configuration for the app's local "active session" storage.
 *
 * - entities: the tables managed by this database.
 * - version: schema version; increment when you change entities/indices/foreign keys and provide a migration.
 */
@Database(
    entities = [RoomSession::class, RoomSessionPlayer::class, RoomSessionScore::class],
    version = 3
)
abstract class DatabaseFactory: RoomDatabase() {
    abstract val sessionDao: SessionDao
    abstract val sessionPlayerDao: SessionPlayerDao
    abstract val sessionScoreDao: SessionScoreDao
}