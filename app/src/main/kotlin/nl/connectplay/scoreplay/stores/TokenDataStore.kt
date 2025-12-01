package nl.connectplay.scoreplay.stores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// creates a datastore inside the context
private val Context.dataStore by preferencesDataStore(name = "auth")


/**
 * A datastore that saves reads, and removes the users JWT token
 *
 * The class helps the app remember the token even when the app is closed
 * The token is stored in DataStore and the Flow wil update whenever the token changes
 */
class TokenDataStore(private val context: Context) {

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // read the token as a Flow so it updates automatically when changed
    val token: Flow<String?> = context.dataStore.data.map { prefs -> prefs[TOKEN_KEY] }

    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    // static private object
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }
}
