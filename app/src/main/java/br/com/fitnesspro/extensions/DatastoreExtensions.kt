package br.com.fitnesspro.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.fitnesspro.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private const val DATA_STORE_SESSION_INFOS = "sessionInfos"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_SESSION_INFOS)

suspend fun DataStore<Preferences>.getUserSession(): User? {
    val key = stringPreferencesKey(DATA_STORE_SESSION_INFOS)
    val data = this.data.first()[key]

    return Gson().fromJson(data, User::class.java)
}

suspend fun DataStore<Preferences>.saveSessionUser(user: User) {
    val key = stringPreferencesKey(DATA_STORE_SESSION_INFOS)

    this.edit { preferences ->
        preferences[key] = Gson().toJson(user)
    }
}

suspend fun DataStore<Preferences>.removeUserSession() {
    val key = stringPreferencesKey(DATA_STORE_SESSION_INFOS)

    this.edit { preferences ->
        preferences.remove(key)
    }
}