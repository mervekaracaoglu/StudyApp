package com.example.studyapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val tag: String? = null,
    val dueDate: Long? = null, // new
    val isCompleted: Boolean = false, // new
    /*val locationLat: Double? = null,
    val locationLng: Double? = null*/
)
