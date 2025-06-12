package com.example.studyapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.studyapp.database.StudySession
import com.example.studyapp.datastore.UserPreferences
import com.example.studyapp.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.map

/**
 * [StudyViewModel] handles all logic for managing study sessions and user preferences.
 * It serves as the communication bridge between UI and the [StudyRepository],
 * exposing reactive [StateFlow] data for Compose to observe.
 *
 * @param application The Application context.
 * @param repository The repository for database operations.
 */

class StudyViewModel(application: Application, private val repository : StudyRepository )
    : AndroidViewModel(application) {

    /**
     * All study sessions from the database as a [StateFlow].
     * The original cold [Flow] is converted to a hot state flow using [stateIn].
     */

    val allSessions: StateFlow<List<StudySession>> =
        repository.getAllSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Adds a new study session to the database.
     *
     * @param session The [StudySession] to insert.
     */
    fun addSession(session: StudySession) {
        viewModelScope.launch {
            repository.insertSession(session)

        }
    }
    /**
     * Deletes a study session from the database.
     *
     * @param session The [StudySession] to delete.
     */
    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    /**
     * Updates an existing study session in the database.
     *
     * @param session The [StudySession] to update.
     */
    fun updateSession(session: StudySession) {
        viewModelScope.launch {
            repository.updateSession(session)
        }
    }
    /**
     * Private mutable state tracking minutes studied today.
     */
    private val _todayStudyMinutes = MutableStateFlow(0)

    /**
     * Public read-only access to today's total study minutes.
     */
    val todayStudyMinutes: StateFlow<Int> = _todayStudyMinutes

    /**
     * Loads total minutes studied today from the repository.
     */
    fun loadTodayStudyMinutes() {
        viewModelScope.launch {
            _todayStudyMinutes.value = repository.getTodayStudyMinutes()
        }
    }

    /**
     * Tracks the current study streak (in days).
     * Increments if a study session exists for each previous day.
     */
    val currentStreak: StateFlow<Int> = allSessions.map { sessions ->
        val dates = sessions
            .map { it.timestamp }
            .map { Date(it) }
            .map { date -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) }
            .toSet()

        var streak = 0
        var calendar = Calendar.getInstance()

        //loops from today backwards until a day is found without a session.
        while (true) {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            if (dates.contains(dateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        streak
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * Computes total minutes studied during the current week.
     */
    val weeklyStudyMinutes: StateFlow<Int> = allSessions
        .map { sessions ->
            val now = Calendar.getInstance() //current time
            val startOfWeek = now.clone() as Calendar
            startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
            //Resets the time to 0 on the first day of the week (depending on locale).
            startOfWeek.set(Calendar.HOUR_OF_DAY, 0)
            startOfWeek.set(Calendar.MINUTE, 0)
            startOfWeek.set(Calendar.SECOND, 0)
            startOfWeek.set(Calendar.MILLISECOND, 0)

            sessions
                //sessions where the timestamp is after startOfWeek
                .filter { it.timestamp >= startOfWeek.timeInMillis }
                //sums those sessions
                .sumOf { it.durationMinutes }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /**
     * Private mutable state for weekly study goal (in minutes).
     */
    private val _weeklyGoalMinutes = MutableStateFlow(150)

    /**
     * Public read-only state for weekly goal.
     */
    val weeklyGoalMinutes: StateFlow<Int> = _weeklyGoalMinutes.asStateFlow()

    /**
     * Loads the saved weekly goal from [UserPreferences] when ViewModel is initialized.
     */
    init {
        viewModelScope.launch {
            UserPreferences.loadWeeklyGoal(application).collect {
                //collects emitted values from the flow
                _weeklyGoalMinutes.value = it
            }
        }
    }

    /**
     * Updates the weekly goal value and persists it via [UserPreferences].
     *
     * @param minutes The new weekly goal in minutes.
     */
    fun setWeeklyGoal(minutes: Int) {
        viewModelScope.launch {
            _weeklyGoalMinutes.value = minutes
            UserPreferences.saveWeeklyGoal(application, minutes) //persist the new value
        }
    }





}
