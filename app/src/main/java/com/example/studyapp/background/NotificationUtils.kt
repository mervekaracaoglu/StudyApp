package com.example.studyapp.background

import android.Manifest
import android.app.*
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.studyapp.PomodoroMode
import com.example.studyapp.R

object NotificationUtils {
    private const val CHANNEL_ID = "pomodoro_channel"

    fun createNotification(context: Context, mode: PomodoroMode, timeMillis: Long): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pomodoro Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Pomodoro ${mode.name}")
            .setContentText("Time remaining: ${formatTime(timeMillis)}")
            .setSmallIcon(R.drawable.analytics_icon)
            .setOngoing(true)
            .build()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun updateNotification(context: Context, mode: PomodoroMode, timeMillis: Long) {
        val notification = createNotification(context, mode, timeMillis)
        NotificationManagerCompat.from(context).notify(1, notification)
    }

    private fun formatTime(ms: Long): String {
        val minutes = (ms / 1000) / 60
        val seconds = (ms / 1000) % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

fun showReminderNotification(context: Context, title: String) {
    val channelId = "reminder_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Reminder")
        .setContentText(title)
        .setSmallIcon(R.drawable.ic_notification) // your icon
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
}
