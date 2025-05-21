package com.example.studyapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.studyapp.database.Reminder
import com.example.studyapp.workmanager.ReminderWorker

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private var reminderId = 0

    fun scheduleReminder(title: String, timeMillis: Long) {
        val reminder = Reminder(id = reminderId++, title = title, timeMillis = timeMillis)
        ReminderWorker.scheduleReminder(getApplication(), reminder)
    }
}
