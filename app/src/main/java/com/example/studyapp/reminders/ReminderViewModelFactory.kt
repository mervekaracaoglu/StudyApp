package com.example.studyapp.reminders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
/**
 * Factory class for creating an instance of [ReminderViewModel] with required dependencies.
 * Used to inject the [Application] context and [ReminderDao] into the ViewModel.
 *
 * @param app The application context used for initializing the database.
 */
class ReminderViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    /**
     * Creates a new instance of [ReminderViewModel].
     *
     * @param T The type of ViewModel requested.
     * @param modelClass The class of the ViewModel to create.
     * @return An instance of [ReminderViewModel] if the class matches.
     * @throws IllegalArgumentException if the ViewModel class is not supported.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            val db = ReminderDatabase.getDatabase(app)
            return ReminderViewModel(app, db.reminderDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

