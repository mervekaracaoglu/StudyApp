package com.example.studyapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.database.StudyDatabase
import com.example.studyapp.database.StudySession
import com.example.studyapp.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class StudyViewModel(private val repository : StudyRepository )
    : ViewModel() {

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

}
