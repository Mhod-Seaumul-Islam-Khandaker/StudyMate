package com.example.studymate.ui.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.model.Priority
import com.example.studymate.data.model.TaskEntity
import com.example.studymate.data.model.TaskStatus
import com.example.studymate.data.repository.TaskRepository
import com.example.studymate.data.repository.UserRepository
import com.example.studymate.ui.common.UiState
import com.example.studymate.utils.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val ttsHelper: TextToSpeechHelper
) : ViewModel() {

    private val TAG = "TasksViewModel"

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    private var currentUserId: Long? = null

    init {
        Log.d(TAG, "init: Initializing")
        loadUserAndTasks()
    }

    private fun loadUserAndTasks() {
        viewModelScope.launch {
            try {
                // For offline app, we ensure we have a default user
                currentUserId = userRepository.getOrCreateDefaultUser()
                Log.d(TAG, "loadUserAndTasks: User ID $currentUserId")
                
                currentUserId?.let { userId ->
                    taskRepository.getTasksForUser(userId)
                        .catch { e ->
                            Log.e(TAG, "Error loading tasks", e)
                            _uiState.value = UiState.Error(e.message ?: "Unknown error")
                        }
                        .collectLatest { taskList ->
                            Log.d(TAG, "Loaded ${taskList.size} tasks")
                            _tasks.value = taskList
                            _uiState.value = if (taskList.isEmpty()) UiState.Empty else UiState.Success(taskList)
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loadUserAndTasks", e)
                _uiState.value = UiState.Error(e.message ?: "Failed to load user")
            }
        }
    }

    fun updateTaskStatus(task: TaskEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isCompleted) TaskStatus.COMPLETED else TaskStatus.PENDING
            val updatedTask = task.copy(status = newStatus)
            Log.d(TAG, "updateTaskStatus: ${task.id} -> $newStatus")
            taskRepository.updateTask(updatedTask)
            
            if (isCompleted) {
                ttsHelper.speak("Task completed: ${task.title}")
            } else {
                ttsHelper.speak("Task marked as pending: ${task.title}")
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            Log.d(TAG, "updateTask: ${task.id} - ${task.title}")
            taskRepository.updateTask(task)
            ttsHelper.speak("Task updated successfully")
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            Log.d(TAG, "deleteTask: ${task.id}")
            taskRepository.deleteTask(task)
            ttsHelper.speak("Task deleted")
        }
    }

    fun addTask(title: String, priority: Priority, dueDate: Long) {
        val userId = currentUserId
        if (userId == null) {
            Log.e(TAG, "addTask: No user ID found")
            return
        }

        viewModelScope.launch {
            val newTask = TaskEntity(
                title = title,
                description = null,
                status = TaskStatus.PENDING,
                dueDate = dueDate,
                priority = priority,
                userId = userId
            )
            Log.d(TAG, "addTask: Adding ${newTask.title}")
            taskRepository.insertTask(newTask)
            ttsHelper.speak("Task added successfully")
        }
    }
}
