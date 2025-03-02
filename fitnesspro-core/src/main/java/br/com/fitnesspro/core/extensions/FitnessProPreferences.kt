package br.com.fitnesspro.core.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "authentication")

object PreferencesKey{
    val USER = stringPreferencesKey("userId")
}

suspend fun DataStore<Preferences>.getAuthenticatedUserId(): String? {
    return this.data.first()[PreferencesKey.USER]
}
