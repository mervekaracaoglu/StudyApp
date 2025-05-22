package com.example.studyapp.reminders

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val message: String,
    val timeInMillis: Long,
    val isRepeating: Boolean
)
