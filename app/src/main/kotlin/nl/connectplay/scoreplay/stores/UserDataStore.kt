package nl.connectplay.scoreplay.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user")

class UserDataStore(private val context: Context) {
    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[USER_KEY] = userId
        }
    }

    val userId: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[USER_KEY]
    }

    suspend fun clearUserId() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_KEY)
        }
    }

    companion object {
        private val USER_KEY = intPreferencesKey("user_id")
    }
}


