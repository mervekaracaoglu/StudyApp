package com.example.studyapp.workmanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Study Reminder"
        NotificationUtils.showReminderNotification(context, title)
        return Result.success()
    }

    companion object {
        fun scheduleReminder(context: Context, reminder: Reminder) {
            val delay = reminder.timeMillis - System.currentTimeMillis()
            if (delay <= 0) return

            val data = Data.Builder()
                .putString("title", reminder.title)
                .build()

            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("reminder_${reminder.id}")
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
