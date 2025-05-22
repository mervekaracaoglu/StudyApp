package com.example.studyapp.reminders

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ReminderViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            val db = ReminderDatabase.getDatabase(app)
            return ReminderViewModel(app, db.reminderDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

