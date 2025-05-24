package com.example.studyapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsDataStore {

    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    suspend fun toggleDarkTheme(context: Context) {
        context.dataStore.edit { preferences ->
            val current = preferences[DARK_THEME_KEY] == true
            preferences[DARK_THEME_KEY] = !current
        }
    }

    fun getSettings(context: Context): Flow<SettingsUiState> {
        return context.dataStore.data.map { preferences ->
            SettingsUiState(
                isDarkTheme = preferences[DARK_THEME_KEY] == true
            )
        }
    }
}
