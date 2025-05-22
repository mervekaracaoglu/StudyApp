package com.example.studyapp.viewModel

import com.example.studyapp.repository.StudyRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application

class StudyViewModelFactory(
    private val application: Application,
    private val repository: StudyRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudyViewModel(application, repository) as T
    }
}

