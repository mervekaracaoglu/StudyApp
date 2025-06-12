package com.example.studyapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Represents a study session entry in the local Room database.
 *
 * @property id Auto-generated primary key for each session.
 * @property subject The subject or topic studied.
 * @property durationMinutes Duration of the session in minutes.
 * @property timestamp The time the session was logged, in milliseconds since epoch.
 * @property notes Optional notes or descriptions for the session.
 * @property tag Optional tag for categorization (e.g., "Exam", "Homework").
 * @property dueDate Optional due date for the task or session, in milliseconds since epoch.
 * @property isCompleted Whether the session/task is marked as completed.
 */
@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val tag: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
)
