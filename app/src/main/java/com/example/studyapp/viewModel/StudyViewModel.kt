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

class StudyViewModel(application: Application, private val repository : StudyRepository )
    : AndroidViewModel(application) {

        //cold flow to StateFlow -> stateIn()
        //cold flow: doesn't emit unless collected
    //takes the result of the repository as flow and turns it into StateFlow
    val allSessions: StateFlow<List<StudySession>> =
        repository.getAllSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addSession(session: StudySession) {
        viewModelScope.launch {
            repository.insertSession(session)

        }
    }
    fun deleteSession(session: StudySession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
    fun updateSession(session: StudySession) {
        viewModelScope.launch {
            repository.updateSession(session)
        }
    }

    private val _todayStudyMinutes = MutableStateFlow(0) //internal mutable state
    val todayStudyMinutes: StateFlow<Int> = _todayStudyMinutes //external read-only state
    //to protect state from being modified outside

    fun loadTodayStudyMinutes() {
        viewModelScope.launch {
            _todayStudyMinutes.value = repository.getTodayStudyMinutes()
        }
    }

    val currentStreak: StateFlow<Int> = allSessions.map { sessions ->
        val dates = sessions
            .map { it.timestamp }
            .map { Date(it) }
            .map { date -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) }
            .toSet()

        var streak = 0
        var calendar = Calendar.getInstance()

        //loops starting from today goes back one day until there are no days
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

    private val _weeklyGoalMinutes = MutableStateFlow(150)
    val weeklyGoalMinutes: StateFlow<Int> = _weeklyGoalMinutes.asStateFlow()

    //init: loads the saved weekly goal on ViewModel creation
    init {
        viewModelScope.launch {
            UserPreferences.loadWeeklyGoal(application).collect {
                //collects emitted values from the flow
                _weeklyGoalMinutes.value = it
            }
        }
    }

    fun setWeeklyGoal(minutes: Int) {
        viewModelScope.launch {
            _weeklyGoalMinutes.value = minutes
            UserPreferences.saveWeeklyGoal(application, minutes) //persist the new value
        }
    }





}
