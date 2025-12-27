package com.example.studymate.ui.goals

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.model.GoalEntity
import com.example.studymate.data.model.GoalStatus
import com.example.studymate.data.repository.GoalRepository
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
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val userRepository: UserRepository,
    private val ttsHelper: TextToSpeechHelper
) : ViewModel() {

    private val TAG = "GoalsViewModel"

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _goals = MutableStateFlow<List<GoalEntity>>(emptyList())
    val goals: StateFlow<List<GoalEntity>> = _goals.asStateFlow()

    private var currentUserId: Long? = null

    init {
        Log.d(TAG, "init: Initializing")
        loadUserAndGoals()
    }

    private fun loadUserAndGoals() {
        viewModelScope.launch {
            try {
                // For offline app, we ensure we have a default user
                currentUserId = userRepository.getOrCreateDefaultUser()
                Log.d(TAG, "loadUserAndGoals: User ID $currentUserId")
                
                currentUserId?.let { userId ->
                    goalRepository.getGoalsForUser(userId)
                        .catch { e ->
                            Log.e(TAG, "Error loading goals", e)
                            _uiState.value = UiState.Error(e.message ?: "Unknown error")
                        }
                        .collectLatest { goalList ->
                            Log.d(TAG, "Loaded ${goalList.size} goals")
                            _goals.value = goalList
                            _uiState.value = if (goalList.isEmpty()) UiState.Empty else UiState.Success(goalList)
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in loadUserAndGoals", e)
                _uiState.value = UiState.Error(e.message ?: "Failed to load user")
            }
        }
    }

    fun updateGoalStatus(goal: GoalEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isCompleted) GoalStatus.COMPLETED else GoalStatus.PENDING
            val updatedGoal = goal.copy(status = newStatus)
            Log.d(TAG, "updateGoalStatus: ${goal.id} -> $newStatus")
            goalRepository.updateGoal(updatedGoal)
            
            if (isCompleted) {
                ttsHelper.speak("Goal completed: ${goal.statement}")
            } else {
                ttsHelper.speak("Goal marked as pending: ${goal.statement}")
            }
        }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            Log.d(TAG, "updateGoal: ${goal.id} - ${goal.statement}")
            goalRepository.updateGoal(goal)
            ttsHelper.speak("Goal updated successfully")
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            Log.d(TAG, "deleteGoal: ${goal.id}")
            goalRepository.deleteGoal(goal)
            ttsHelper.speak("Goal deleted")
        }
    }

    fun addGoal(statement: String) {
        val userId = currentUserId
        if (userId == null) {
            Log.e(TAG, "addGoal: No user ID found")
            return
        }

        viewModelScope.launch {
            val newGoal = GoalEntity(
                statement = statement,
                status = GoalStatus.PENDING,
                userId = userId
            )
            Log.d(TAG, "addGoal: Adding ${newGoal.statement}")
            goalRepository.insertGoal(newGoal)
            ttsHelper.speak("Goal added successfully")
        }
    }
}
