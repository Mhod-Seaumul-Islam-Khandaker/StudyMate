package com.example.studymate.data.repository

import android.util.Log
import com.example.studymate.data.local.dao.GoalDao
import com.example.studymate.data.model.GoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    private val TAG = "GoalRepository"

    fun getGoalsForUser(userId: Long): Flow<List<GoalEntity>> {
        Log.d(TAG, "getGoalsForUser: fetching goals for user $userId")
        return goalDao.getGoalsForUser(userId)
    }

    suspend fun insertGoal(goal: GoalEntity): Long {
        Log.d(TAG, "insertGoal: inserting goal ${goal.statement}")
        return goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) {
        Log.d(TAG, "updateGoal: updating goal ${goal.id}")
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) {
        Log.d(TAG, "deleteGoal: deleting goal ${goal.id}")
        goalDao.deleteGoal(goal)
    }
}
