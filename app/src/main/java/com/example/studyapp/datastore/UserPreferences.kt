package com.example.studyapp.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val WEEKLY_GOAL_KEY = intPreferencesKey("weekly_goal_minutes")

    suspend fun saveWeeklyGoal(context: Context, minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[WEEKLY_GOAL_KEY] = minutes
        }
    }
    fun loadWeeklyGoal(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { prefs -> prefs[WEEKLY_GOAL_KEY] ?: 150 }
    }
}