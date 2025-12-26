package com.example.studymate.ui.timer

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymate.service.TimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = "TimerViewModel"

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    // Timer modes: 25m, 5m, 15m
    private val _selectedDuration = MutableStateFlow(25 * 60 * 1000L)
    val selectedDuration: StateFlow<Long> = _selectedDuration.asStateFlow()

    init {
        Log.d(TAG, "init: Connecting to TimerService")
        viewModelScope.launch {
            TimerService.timeLeft.collect {
                _timeLeft.value = it
            }
        }
        viewModelScope.launch {
            TimerService.isRunning.collect {
                _isRunning.value = it
            }
        }
    }
    
    fun setDuration(duration: Long) {
        if (!_isRunning.value) {
            _selectedDuration.value = duration
            _timeLeft.value = duration
            Log.d(TAG, "setDuration: $duration")
        }
    }

    fun startTimer() {
        Log.d(TAG, "startTimer")
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_START
            putExtra(TimerService.EXTRA_DURATION, _selectedDuration.value)
        }
        context.startService(intent)
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer")
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun stopTimer() {
        Log.d(TAG, "stopTimer")
        val intent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        context.startService(intent)
        _timeLeft.value = _selectedDuration.value
    }
}
