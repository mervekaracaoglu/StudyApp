
package com.example.studyapp.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsDataStore {
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    suspend fun saveThemePreference(context: Context, isDarkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isDarkTheme
        }
    }

    fun getSettings(context: Context): Flow<SettingsUiState> {
        return context.dataStore.data.map { preferences ->
            SettingsUiState(
                isDarkTheme = preferences[DARK_THEME_KEY] ?: false
            )
        }
    }
}
