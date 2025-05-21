package com.example.studyapp.background

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.example.studyapp.PomodoroMode
import kotlinx.coroutines.*

class PomodoroService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var timerJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val duration = intent?.getLongExtra("duration", 25 * 60 * 1000L) ?: return START_NOT_STICKY

        startForeground(1, NotificationUtils.createNotification(this, PomodoroMode.WORK, duration))

        timerJob = scope.launch {
            var remaining = duration
            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                if (ActivityCompat.checkSelfPermission(
                        this as Context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                )
                NotificationUtils.updateNotification(this@PomodoroService, PomodoroMode.WORK, remaining)
            }
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timerJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
