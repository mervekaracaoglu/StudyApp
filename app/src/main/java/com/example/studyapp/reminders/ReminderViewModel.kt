package com.example.studyapp.reminders

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
/**
 * ViewModel for managing reminders using AlarmManager and Room database.
 *
 * @property app The application context used for system services and operations.
 * @property dao The ReminderDao used for accessing reminder data from Room database.
 */
class ReminderViewModel(
    private val app: Application,
    private val dao: ReminderDao
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<ReminderEntity>>(emptyList())

    /** Public state flow exposing all current reminders. */
    val reminders: StateFlow<List<ReminderEntity>> = _reminders.asStateFlow()

    init {
        viewModelScope.launch {
            _reminders.value = dao.getAll()
        }
    }

    /**
     * Schedules a reminder using Android's AlarmManager.
     *
     * @param title The title of the reminder.
     * @param message The message body of the reminder.
     * @param timeInMillis The time (in milliseconds) when the alarm should trigger.
     * @param isRepeating Whether the alarm should repeat daily.
     *
     * @throws SecurityException if exact alarms are not allowed and the request is blocked.
     */
    fun scheduleReminder(title: String, message: String, timeInMillis: Long, isRepeating: Boolean) {
        val context = app.applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(context, "Please allow exact alarms in settings", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        //create the broadcast intent , targets ReminderReceiver (BroadCastReceiver)
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        //wraps the intent in a PendingIntent so AlarmManager can fire even if the app is not running
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (isRepeating) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }

            val reminder = ReminderEntity(
                id = timeInMillis.toInt(),
                title = title,
                message = message,
                timeInMillis = timeInMillis,
                isRepeating = isRepeating
            )

            //save the room and update ui
            viewModelScope.launch {
                dao.insert(reminder)
                _reminders.value = dao.getAll()
            }

            Toast.makeText(context, "Reminder scheduled", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Log.e("ReminderDebug", "SecurityException: Cannot schedule exact alarm", e)
        }
    }
    /**
     * Cancels a previously scheduled reminder and removes it from the database.
     *
     * @param reminder The [ReminderEntity] to cancel and delete.
     */
    fun cancelReminder(reminder: ReminderEntity) {
        val context = app.applicationContext
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("message", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        viewModelScope.launch {
            dao.delete(reminder)
            _reminders.value = dao.getAll()
        }

        Toast.makeText(context, "Reminder cancelled", Toast.LENGTH_SHORT).show()
    }
}
