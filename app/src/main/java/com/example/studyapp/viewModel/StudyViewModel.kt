package com.example.studyapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.database.StudyDatabase
import com.example.studyapp.database.StudySession
import com.example.studyapp.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class StudyViewModel(private val repository : StudyRepository )
    : ViewModel() {

    private val _sessions = MutableStateFlow<List<StudySession>>(emptyList())
    val sessions: StateFlow<List<StudySession>> = _sessions.asStateFlow()

    fun loadSessions() {
        viewModelScope.launch {
            repository.getAllSessions().collect { sessionList ->
                _sessions.value = sessionList
            }
        }
    }
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
}
