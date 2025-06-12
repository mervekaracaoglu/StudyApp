package com.example.studyapp.timer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class for creating instances of [PomodoroViewModel] with a [Context] dependency.
 *
 * This enables the [PomodoroViewModel] to be initialized with an application context,
 * which is useful for features like alarms, notifications, or data persistence.
 *
 * @property context The context used to initialize the ViewModel.
 */
class PomodoroViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    /**
     * Creates an instance of [PomodoroViewModel].
     *
     * @param T The type of ViewModel requested.
     * @param modelClass The class of the ViewModel to be created.
     * @return A [PomodoroViewModel] instance.
     * @throws IllegalArgumentException if the model class is not [PomodoroViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PomodoroViewModel(context.applicationContext) as T
    }
}
