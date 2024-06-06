package id.dimas.fiazenify.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject

class UserPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

//    suspend fun login(remember: Boolean) {
//        dataStore.edit {
//            it[IS_LOGIN] = remember
//        }
//    }

    suspend fun logout() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        private val IS_LOGIN = booleanPreferencesKey("isLogin")
    }
}
