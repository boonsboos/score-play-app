package nl.connectplay.scoreplay.database

import androidx.room.Database
import androidx.room.RoomDatabase
import nl.connectplay.scoreplay.dao.SessionDao
import nl.connectplay.scoreplay.models.session.Session

@Database(
    entities = [Session::class],
    version = 1
)
abstract class SessionDatabase: RoomDatabase() {

    abstract val dao: SessionDao
}