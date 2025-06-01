package com.example.studyapp.timer

import android.app.*
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.studyapp.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class PomodoroService : LifecycleService() {

    private val channelId = "pomodoro_channel_v1"
    private val notificationId = 1

    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    //default = background , SupervisorJob() ensures if one coroutine failure it doesn't cancel the entire scope

    private val state = MutableStateFlow(PomodoroState(timeLeftMillis = 1 * 60 * 1000L))

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d("PomodoroService", "Received intent: ${intent?.action}")
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESET -> resetTimer()
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
        //if the service is killed by the system, restart it as soon as possible with a null intent
    }

    private fun startTimer() {
        if (timerJob != null) return

        timerJob = serviceScope.launch {
            state.update { it.copy(isRunning = true) }

            while (state.value.timeLeftMillis > 0) {
                delay(1000)
                state.update { it.copy(timeLeftMillis = it.timeLeftMillis - 1000) }
                //copy -> creates a new immutable instance of PomodoroState
                broadcastStateUpdate() //sends a local broadcast intent
                updateNotification() //updates the ongoing notification with new time
            }
            onTimerFinish()
        }

        startForeground(notificationId, buildNotification(state.value))
        //shows a persistent notification while the service runs in the foreground
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        state.update { it.copy(isRunning = false) }
        broadcastStateUpdate()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        state.update { it.copy(timeLeftMillis = it.focusDuration, isRunning = false) }
        broadcastStateUpdate()
        updateNotification()
    }

    private fun onTimerFinish() {
        timerJob = null
        val newState = state.value.let {
            val nextSessionCount = if (!it.isBreak) it.sessionCount + 1 else it.sessionCount
            val isBreak = !it.isBreak
            val isLongBreak = isBreak && nextSessionCount % 4 == 0
            val nextTime = when {
                isLongBreak -> it.longBreakDuration
                isBreak -> it.shortBreakDuration
                else -> it.focusDuration
            }
            it.copy(
                timeLeftMillis = nextTime,
                isRunning = false,
                isBreak = isBreak,
                isLongBreak = isLongBreak,
                sessionCount = nextSessionCount
            )
        }
        state.value = newState
        playEndSound()
        broadcastStateUpdate()
        updateNotification()
    }

    private fun broadcastStateUpdate() {
        val intent = Intent(ACTION_UPDATE_STATE).apply {
            putExtra(EXTRA_TIME_LEFT, state.value.timeLeftMillis)
            putExtra(EXTRA_IS_RUNNING, state.value.isRunning)
            putExtra(EXTRA_SESSION_COUNT, state.value.sessionCount)
            putExtra(EXTRA_IS_BREAK, state.value.isBreak)
            putExtra(EXTRA_IS_LONG_BREAK, state.value.isLongBreak)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun buildNotification(current: PomodoroState): Notification {
        val minutes = (current.timeLeftMillis / 1000) / 60
        val seconds = (current.timeLeftMillis / 1000) % 60
        val formattedTime = String.format(Locale.US, "%02d:%02d", minutes, seconds)

        val pauseIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_PAUSE }
        val resetIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_RESET }
        val stopIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_STOP }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Time left: $formattedTime")
            .setContentText(if (current.isBreak) "Break time" else "Focus time")
            .setSmallIcon(R.drawable.studying)
            .setOngoing(true)
            .addAction(0, "Pause", PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            .addAction(0, "Reset", PendingIntent.getService(this, 2, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            .addAction(0, "Stop", PendingIntent.getService(this, 3, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            .build()
    }

    private fun updateNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, buildNotification(state.value))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(channelId, "Pomodoro Timer", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onDestroy() {
        timerJob?.cancel()
        timerJob = null
        state.update { it.copy(isRunning = false) }
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun playEndSound() {
        try {
            val ringtoneUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
            val ringtone = android.media.RingtoneManager.getRingtone(applicationContext, ringtoneUri)
            ringtone?.play()
        } catch (e: Exception) {
            Log.e("PomodoroService", "Failed to play end sound", e)
        }
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESET = "ACTION_RESET"
        const val ACTION_STOP = "ACTION_STOP"

        const val ACTION_UPDATE_STATE = "POMODORO_STATE_UPDATE"
        const val EXTRA_TIME_LEFT = "EXTRA_TIME_LEFT"
        const val EXTRA_IS_RUNNING = "EXTRA_IS_RUNNING"
        const val EXTRA_SESSION_COUNT = "EXTRA_SESSION_COUNT"
        const val EXTRA_IS_BREAK = "EXTRA_IS_BREAK"
        const val EXTRA_IS_LONG_BREAK = "EXTRA_IS_LONG_BREAK"
    }
}
