package com.example.studyapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.studyapp.database.StudySession
import com.example.studyapp.settings.UserPreferences
import com.example.studyapp.repository.StudyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    val allSessions: StateFlow<List<StudySession>> =
        repository.getAllSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSession(session: StudySession) {
        viewModelScope.launch {
            repository.insertSession(session)
            syncSessionToFirestore(session)

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

    private val _todayStudyMinutes = MutableStateFlow(0)
    val todayStudyMinutes: StateFlow<Int> = _todayStudyMinutes

    fun loadTodayStudyMinutes() {
        viewModelScope.launch {
            _todayStudyMinutes.value = repository.getTodayStudyMinutes()
        }
    }

    val currentStreak: StateFlow<Int> = allSessions.map { sessions ->
        val dates = sessions
            .mapNotNull { it.timestamp }
            .map { Date(it) }
            .map { date -> SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) }
            .toSet()

        var streak = 0
        var calendar = Calendar.getInstance()

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
            val now = Calendar.getInstance()
            val startOfWeek = now.clone() as Calendar
            startOfWeek.set(Calendar.DAY_OF_WEEK, startOfWeek.firstDayOfWeek)
            startOfWeek.set(Calendar.HOUR_OF_DAY, 0)
            startOfWeek.set(Calendar.MINUTE, 0)
            startOfWeek.set(Calendar.SECOND, 0)
            startOfWeek.set(Calendar.MILLISECOND, 0)

            sessions
                .filter { it.timestamp >= startOfWeek.timeInMillis }
                .sumOf { it.durationMinutes }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _weeklyGoalMinutes = MutableStateFlow(150)
    val weeklyGoalMinutes: StateFlow<Int> = _weeklyGoalMinutes.asStateFlow()

    init {
        viewModelScope.launch {
            UserPreferences.loadWeeklyGoal(application).collect {
                _weeklyGoalMinutes.value = it
            }
        }
    }

    fun setWeeklyGoal(minutes: Int) {
        viewModelScope.launch {
            _weeklyGoalMinutes.value = minutes
            UserPreferences.saveWeeklyGoal(application, minutes)
        }
    }

    private fun syncSessionToFirestore(session: StudySession) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val sessionData = hashMapOf(
            "subject" to session.subject,
            "durationMinutes" to session.durationMinutes,
            "timestamp" to session.timestamp
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("sessions")
            .add(sessionData)
    }

    fun loadSessionsFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("sessions")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Firestore", "Fetched ${result.size()} documents")
                viewModelScope.launch {
                    for (doc in result) {
                        val subject = doc.getString("subject") ?: continue
                        val duration = doc.getLong("durationMinutes")?.toInt() ?: continue
                        val timestamp = doc.getLong("timestamp") ?: continue

                        val session = StudySession(subject = subject, durationMinutes = duration, timestamp = timestamp)
                        repository.insertSession(session)
                    }
                }
            }
    }






}
