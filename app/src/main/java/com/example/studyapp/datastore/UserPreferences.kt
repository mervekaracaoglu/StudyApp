package com.example.studyapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
/**
 * Manages user preferences related to study goals using Jetpack DataStore.
 */
object UserPreferences {
    private val WEEKLY_GOAL_KEY = intPreferencesKey("weekly_goal_minutes")
    /**
     * Saves the user's weekly goal in minutes to DataStore.
     *
     * @param context The application context used to access DataStore.
     * @param minutes The number of minutes to set as the weekly study goal.
     */
    suspend fun saveWeeklyGoal(context: Context, minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[WEEKLY_GOAL_KEY] = minutes
        }

    }

    /**
     * Loads the user's weekly study goal from DataStore as a [Flow].
     * Emits a default value of 150 if no value is stored yet.
     *
     * @param context The application context used to access DataStore.
     * @return A [Flow] emitting the weekly goal in minutes.
     */
    fun loadWeeklyGoal(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { prefs -> prefs[WEEKLY_GOAL_KEY] ?: 150 }
    }
}