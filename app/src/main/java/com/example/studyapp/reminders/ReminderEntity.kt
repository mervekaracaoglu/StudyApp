package com.example.studyapp.reminders

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Data class representing a scheduled reminder.
 *
 * This entity is stored in the Room database and contains metadata
 * about the reminder, including its scheduled time and repeat status.
 *
 * @property id Unique identifier for the reminder (used as the primary key).
 * @property title Title to be shown in the notification.
 * @property message Message body of the reminder.
 * @property timeInMillis Time at which the reminder should trigger, in milliseconds since epoch.
 * @property isRepeating Whether the reminder repeats daily.
 */
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val message: String,
    val timeInMillis: Long,
    val isRepeating: Boolean
)
