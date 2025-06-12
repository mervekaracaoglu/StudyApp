package com.example.studyapp.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.studyapp.R
import android.util.Log
/**
 * BroadcastReceiver that handles scheduled reminder broadcasts and displays a notification.
 *
 * This receiver is triggered by the AlarmManager at the specified time, and builds
 * a notification using the title and message passed via the Intent extras.
 */
class ReminderReceiver : BroadcastReceiver() {
    /**
     * Called when the broadcast is received. Builds and displays the notification.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent received from AlarmManager with extras: "title" and "message".
     */
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderDebug", "Reminder broadcast received!")

        val title = intent.getStringExtra("title") ?: "Study Reminder"
        val message = intent.getStringExtra("message") ?: "Time to study!"

        val channelId = "reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //create notification channel
        val channel = NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        //build notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.studying)
            .setAutoCancel(true)
            .build()

        //show notification with unique id
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
