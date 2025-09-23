package br.com.fitnesspro.core.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStorePreferences")

object PreferencesKey {
    val USER = stringPreferencesKey("userId")
    val RUN_EXPORT_WORKER = booleanPreferencesKey("runExportWorker")
    val RUN_IMPORT_WORKER = booleanPreferencesKey("runImportWorker")
}

suspend fun DataStore<Preferences>.getAuthenticatedUserId(): String? {
    return this.data.first()[PreferencesKey.USER]
}

suspend fun DataStore<Preferences>.getRunExportWorker(): Boolean {
    return this.data.first()[PreferencesKey.RUN_EXPORT_WORKER] ?: true
}

suspend fun DataStore<Preferences>.setRunExportWorker(value: Boolean) {
    this.updateData { preferences ->
        preferences.toMutablePreferences().apply {
            set(PreferencesKey.RUN_EXPORT_WORKER, value)
        }
    }
}

suspend fun DataStore<Preferences>.getRunImportWorker(): Boolean {
    return this.data.first()[PreferencesKey.RUN_IMPORT_WORKER] ?: true
}

suspend fun DataStore<Preferences>.setRunImportWorker(value: Boolean) {
    this.updateData { preferences ->
        preferences.toMutablePreferences().apply {
            set(PreferencesKey.RUN_IMPORT_WORKER, value)
        }
    }
}