package nl.connectplay.scoreplay.room

import androidx.room.Database
import androidx.room.RoomDatabase
import nl.connectplay.scoreplay.room.dao.SessionDao
import nl.connectplay.scoreplay.room.entities.RoomSession

@Database(
    entities = [RoomSession::class],
    version = 2
)
abstract class Database: RoomDatabase() {

    abstract val dao: SessionDao
}