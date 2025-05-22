package com.example.studyapp.timer

data class PomodoroState(
    val timeLeftMillis: Long,
    val isRunning: Boolean = false,
    val sessionCount: Int = 0,
    val isBreak: Boolean = false,
    val isLongBreak: Boolean = false,
    val focusDuration: Long = 25 * 60 * 1000L,
    val shortBreakDuration: Long = 5 * 60 * 1000L,
    val longBreakDuration: Long = 15 * 60 * 1000L,
)
