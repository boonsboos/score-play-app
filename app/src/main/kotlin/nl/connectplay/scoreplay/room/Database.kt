package nl.connectplay.scoreplay.room

import androidx.room.Database
import androidx.room.RoomDatabase
import nl.connectplay.scoreplay.room.dao.SessionDao
import nl.connectplay.scoreplay.room.dao.SessionPlayerDao
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

@Database(
    entities = [RoomSession::class, RoomSessionPlayer::class],
    version = 1
)
abstract class Database: RoomDatabase() {
    abstract val sessionDao: SessionDao
    abstract val sessionPlayerDao: SessionPlayerDao
}