package com.example.studymate.ui.timer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studymate.databinding.FragmentTimerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TimerViewModel by viewModels()
    private val TAG = "TimerFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView: Inflating layout")
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnPomodoro.setOnClickListener {
            Log.d(TAG, "Selected 25m")
            viewModel.setDuration(25 * 60 * 1000L)
        }

        binding.btnShortBreak.setOnClickListener {
            Log.d(TAG, "Selected 5m")
            viewModel.setDuration(5 * 60 * 1000L)
        }

        binding.btnLongBreak.setOnClickListener {
            Log.d(TAG, "Selected 15m")
            viewModel.setDuration(15 * 60 * 1000L)
        }

        binding.btnStart.setOnClickListener {
            Log.d(TAG, "Start clicked")
            viewModel.startTimer()
        }

        binding.btnStop.setOnClickListener {
            Log.d(TAG, "Stop clicked")
            viewModel.stopTimer()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.timeLeft.collectLatest { millis ->
                        updateTimerText(millis)
                    }
                }
                launch {
                    viewModel.isRunning.collectLatest { isRunning ->
                        updateUiState(isRunning)
                    }
                }
            }
        }
    }

    private fun updateTimerText(millis: Long) {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000) % 60
        val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.textTimer.text = timeString
    }

    private fun updateUiState(isRunning: Boolean) {
        binding.btnStart.isEnabled = !isRunning
        binding.btnStop.isEnabled = isRunning
        binding.btnPomodoro.isEnabled = !isRunning
        binding.btnShortBreak.isEnabled = !isRunning
        binding.btnLongBreak.isEnabled = !isRunning
        
        binding.timerStatus.text = if (isRunning) "Focusing..." else "Ready to focus"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
