package com.example.studyapp.viewModel

import com.example.studyapp.repository.StudyRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application

/**
 * Factory class responsible for creating an instance of [StudyViewModel]
 * with required dependencies injected.
 *
 * This allows the ViewModel to receive the [Application] context and a [StudyRepository]
 * instance, making it testable, lifecycle-aware, and compatible with `by viewModels()` or `viewModelProvider()`.
 *
 * @property application The application context, passed for use in the ViewModel.
 * @property repository The repository handling data operations for study sessions.
 */
class StudyViewModelFactory(
    private val application: Application,
    private val repository: StudyRepository
) : ViewModelProvider.Factory {

    /**
     * Creates and returns an instance of [StudyViewModel] with injected dependencies.
     *
     * @param T The type of ViewModel.
     * @param modelClass The class of the ViewModel to be created.
     * @return A new instance of [StudyViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudyViewModel(application, repository) as T
    }
}

