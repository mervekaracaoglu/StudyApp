package com.example.studyapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    private var timerJob: Job? = null

    fun startTimer(durationMillis: Long, mode: PomodoroMode) {
        timerJob?.cancel()
        _timerState.value = TimerState(durationMillis, durationMillis, isRunning = true, mode)

        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingTime > 0) {
                delay(1000L)
                _timerState.update {
                    it.copy(remainingTime = it.remainingTime - 1000L)
                }
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerState.update {
            it.copy(remainingTime = it.totalTime, isRunning = false)
        }
    }
}

data class TimerState(
    val totalTime: Long = 25 * 60 * 1000L,
    val remainingTime: Long = 25 * 60 * 1000L,
    val isRunning: Boolean = false,
    val mode: PomodoroMode = PomodoroMode.WORK
)

enum class PomodoroMode {
    WORK, BREAK, LONG_BREAK
}
