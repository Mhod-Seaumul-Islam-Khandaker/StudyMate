package com.example.studymate.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.model.TaskStatus
import com.example.studymate.data.repository.TaskRepository
import com.example.studymate.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = "DashboardViewModel"
    private val _userId = MutableStateFlow<Long?>(null)

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount.asStateFlow()

    private val _overdueCount = MutableStateFlow(0)
    val overdueCount: StateFlow<Int> = _overdueCount.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    init {
        Log.d(TAG, "init: Initializing DashboardViewModel")
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val id = userRepository.getOrCreateDefaultUser()
            _userId.value = id
            Log.d(TAG, "loadData: User ID is $id")

            launch {
                taskRepository.getPendingTaskCount(id).collectLatest {
                    Log.d(TAG, "Pending count: $it")
                    _pendingCount.value = it
                    updateProgress()
                }
            }

            launch {
                taskRepository.getTasksByStatus(id, TaskStatus.COMPLETED).collectLatest {
                    Log.d(TAG, "Completed count: ${it.size}")
                    _completedCount.value = it.size
                    updateProgress()
                }
            }

            launch {
                taskRepository.getOverdueTasks(id).collectLatest {
                    Log.d(TAG, "Overdue count: ${it.size}")
                    _overdueCount.value = it.size
                }
            }
        }
    }

    private fun updateProgress() {
        val total = _pendingCount.value + _completedCount.value
        val percent = if (total > 0) {
            (_completedCount.value.toFloat() / total.toFloat() * 100).toInt()
        } else {
            0
        }
        _progress.value = percent
    }
}
