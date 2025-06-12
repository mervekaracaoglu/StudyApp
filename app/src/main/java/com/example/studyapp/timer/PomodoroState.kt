package com.example.studyapp.timer

/**
 * Represents the current state of the Pomodoro timer.
 * This state is held in a [StateFlow] and observed by the UI.
 *
 * @property timeLeftMillis The time remaining in the current session (in milliseconds).
 * @property isRunning Whether the timer is currently running.
 * @property sessionCount Number of completed focus sessions.
 * @property isBreak Whether the current session is a break.
 * @property isLongBreak Whether the current break is a long break.
 * @property focusDuration Duration of a focus session (default: 25 minutes).
 * @property shortBreakDuration Duration of a short break (default: 5 minutes).
 * @property longBreakDuration Duration of a long break (default: 15 minutes).
 */
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
