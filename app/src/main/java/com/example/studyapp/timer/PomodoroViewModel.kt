package com.example.studyapp.timer

import android.content.*
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
/**
 * [PomodoroViewModel] manages the UI state for the Pomodoro timer.
 * It communicates with [PomodoroService] using local broadcasts and updates its
 * state based on received timer data. It also provides methods to control the service.
 *
 * @param context The application context used for broadcasting and starting services.
 */
class PomodoroViewModel(private val context: Context) : ViewModel() {

    /**
     * Internal mutable state for the Pomodoro timer.
     */
    private val _state = MutableStateFlow(PomodoroState(timeLeftMillis = 25 * 60 * 1000L))

    /**
     * Exposed immutable [StateFlow] for observing timer state in the UI.
     */
    val state: StateFlow<PomodoroState> = _state

    /**
     * BroadcastReceiver that listens for updates from [PomodoroService].
     * It extracts timer information from the intent and updates the ViewModel state.
     */
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

    /**
     * Registers the [broadcastReceiver] to listen for service updates when the ViewModel is created.
     */
    init {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            broadcastReceiver,
            IntentFilter(PomodoroService.ACTION_UPDATE_STATE)
        )
    }

    /**
     * Unregisters the broadcast receiver when the ViewModel is destroyed.
     * Prevents memory leaks and unintended updates.
     */
    override fun onCleared() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver)
        super.onCleared()
    }

    /**
     * Sends a control intent to the [PomodoroService] to perform actions like start, pause, or reset.
     *
     * @param action The action string to be handled by the service.
     */
    fun controlForegroundService(action: String) {
        Log.d("PomodoroViewModel", "Sending intent with action: $action")
        val intent = Intent(context, PomodoroService::class.java).apply {
            this.action = action
        }
        context.startForegroundService(intent)
        //triggers onStartCommand() in PomodoroService
    }
}
