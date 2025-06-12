package com.example.studyapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extension property to access the [DataStore] instance for the application context.
 */
val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Singleton object responsible for managing app-wide settings using Jetpack DataStore.
 */
object SettingsDataStore {

    // Key for storing the dark theme preference
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")

    /**
     * Toggles the current dark theme preference.
     *
     * If dark mode is currently enabled, this will disable it, and vice versa.
     *
     * @param context The application [Context] used to access the DataStore.
     */
    suspend fun toggleDarkTheme(context: Context) {
        context.dataStore.edit { preferences ->
            val current = preferences[DARK_THEME_KEY] == true
            preferences[DARK_THEME_KEY] = !current
        }
    }

    /**
     * Returns a [Flow] that emits [SettingsUiState] based on the current app settings.
     *
     * This allows Compose or other reactive components to observe and react to changes.
     *
     * @param context The application [Context] used to access the DataStore.
     * @return A [Flow] of [SettingsUiState] reflecting the current preferences.
     */
    fun getSettings(context: Context): Flow<SettingsUiState> {
        return context.dataStore.data.map { preferences ->
            SettingsUiState(
                isDarkTheme = preferences[DARK_THEME_KEY] == true
            )
        }
    }
}
