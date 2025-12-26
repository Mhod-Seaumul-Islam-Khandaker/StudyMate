package com.example.studymate.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.studymate.R
import com.example.studymate.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var userRepository: UserRepository

    private val TAG = "TimerService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timer: CountDownTimer? = null

    companion object {
        const val CHANNEL_ID = "StudyTimerChannel"
        const val NOTIFICATION_ID = 1

        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        
        const val EXTRA_DURATION = "EXTRA_DURATION"

        // Shared state for UI to observe
        private val _timeLeft = MutableStateFlow(0L)
        val timeLeft: StateFlow<Long> = _timeLeft.asStateFlow()

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            ACTION_START -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, 25 * 60 * 1000L)
                startTimer(duration)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(duration: Long) {
        if (_isRunning.value) return

        Log.d(TAG, "startTimer: Starting for $duration ms")
        
        // If we are resuming, use current timeLeft
        val timeToRun = if (_timeLeft.value > 0 && _timeLeft.value < duration) _timeLeft.value else duration
        
        _isRunning.value = true
        startForeground(NOTIFICATION_ID, buildNotification(timeToRun))

        timer = object : CountDownTimer(timeToRun, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished
                Log.d(TAG, "onTick: $millisUntilFinished")
                updateNotification(millisUntilFinished)
            }

            override fun onFinish() {
                Log.d(TAG, "onFinish: Timer completed")
                _timeLeft.value = 0
                _isRunning.value = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                // TODO: Save completed session to DB via Repository
            }
        }.start()
    }

    private fun pauseTimer() {
        Log.d(TAG, "pauseTimer: Pausing")
        timer?.cancel()
        _isRunning.value = false
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    private fun stopTimer() {
        Log.d(TAG, "stopTimer: Stopping")
        timer?.cancel()
        _timeLeft.value = 0
        _isRunning.value = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Study Timer",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(timeLeft: Long): Notification {
        val minutes = timeLeft / 1000 / 60
        val seconds = (timeLeft / 1000) % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Study Timer")
            .setContentText("Time remaining: $timeString")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Use standard android icon for now
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
    
    private fun updateNotification(timeLeft: Long) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(timeLeft))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Service destroyed")
        timer?.cancel()
    }
}
