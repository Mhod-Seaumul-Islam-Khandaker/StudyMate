package com.example.studymate.ui.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.model.GoalStatus
import com.example.studymate.data.model.TaskStatus
import com.example.studymate.data.repository.GoalRepository
import com.example.studymate.data.repository.TaskRepository
import com.example.studymate.data.repository.UserRepository
import com.example.studymate.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = "ProgressViewModel"

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _progressPercentage = MutableStateFlow(0)
    val progressPercentage: StateFlow<Int> = _progressPercentage.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems.asStateFlow()

    private val _completedItems = MutableStateFlow(0)
    val completedItems: StateFlow<Int> = _completedItems.asStateFlow()

    init {
        Log.d(TAG, "init: Initializing ProgressViewModel")
        loadProgress()
    }

    private fun loadProgress() {
        viewModelScope.launch {
            try {
                val userId = userRepository.getOrCreateDefaultUser()
                
                // Combine tasks and goals to calculate overall progress
                combine(
                    taskRepository.getTasksForUser(userId),
                    goalRepository.getGoalsForUser(userId)
                ) { tasks, goals ->
                    val totalTasks = tasks.size
                    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
                    
                    val totalGoals = goals.size
                    val completedGoals = goals.count { it.status == GoalStatus.COMPLETED }
                    
                    val total = totalTasks + totalGoals
                    val completed = completedTasks + completedGoals
                    
                    Triple(total, completed, true)
                }.collect { (total, completed, _) ->
                    _totalItems.value = total
                    _completedItems.value = completed
                    
                    val percentage = if (total > 0) {
                        (completed.toFloat() / total.toFloat() * 100).toInt()
                    } else {
                        0
                    }
                    
                    _progressPercentage.value = percentage
                    _uiState.value = UiState.Success(percentage)
                    Log.d(TAG, "Progress updated: $percentage% ($completed/$total)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating progress", e)
                _uiState.value = UiState.Error(e.message ?: "Failed to calculate progress")
            }
        }
    }
}
