package com.example.studyapp.timer

import android.content.*
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PomodoroViewModel(private val context: Context) : ViewModel() {

    private val _state = MutableStateFlow(PomodoroState(timeLeftMillis = 1 * 60 * 1000L))
    val state: StateFlow<PomodoroState> = _state

    //listens for broadcasted updates from PomodoroService
    //extracts relevant data from intent
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PomodoroService.ACTION_UPDATE_STATE) {
                val timeLeft = intent.getLongExtra(PomodoroService.EXTRA_TIME_LEFT, _state.value.timeLeftMillis)
                val isRunning = intent.getBooleanExtra(PomodoroService.EXTRA_IS_RUNNING, _state.value.isRunning)
                val sessionCount = intent.getIntExtra(PomodoroService.EXTRA_SESSION_COUNT, _state.value.sessionCount)
                val isBreak = intent.getBooleanExtra(PomodoroService.EXTRA_IS_BREAK, _state.value.isBreak)
                val isLongBreak = intent.getBooleanExtra(PomodoroService.EXTRA_IS_LONG_BREAK, _state.value.isLongBreak)

                //updates the ViewModel state
                _state.update {
                    it.copy(
                        timeLeftMillis = timeLeft,
                        isRunning = isRunning,
                        sessionCount = sessionCount,
                        isBreak = isBreak,
                        isLongBreak = isLongBreak
                    )
                }
            }
        }
    }

    //registers the broadcast listener when the viewmodel is created
    init {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            broadcastReceiver,
            IntentFilter(PomodoroService.ACTION_UPDATE_STATE)
        )
    }

    //unregisters listeners when viewmodel is destroyed
    override fun onCleared() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
        super.onCleared()
    }

    //sending control commands to the Pomodoro Service
    fun controlForegroundService(action: String) {
        Log.d("PomodoroViewModel", "Sending intent with action: $action")
        val intent = Intent(context, PomodoroService::class.java).apply {
            this.action = action
        }
        context.startForegroundService(intent)
        //triggers onStartCommand() in PomodoroService
    }
}
