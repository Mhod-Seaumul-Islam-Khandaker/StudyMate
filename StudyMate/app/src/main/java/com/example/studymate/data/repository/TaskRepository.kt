package com.example.studymate.data.repository

import android.util.Log
import com.example.studymate.data.local.dao.TaskDao
import com.example.studymate.data.model.TaskEntity
import com.example.studymate.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    private val TAG = "TaskRepository"

    fun getTasksForUser(userId: Long): Flow<List<TaskEntity>> {
        Log.d(TAG, "getTasksForUser: fetching tasks for user $userId")
        return taskDao.getTasksForUser(userId)
    }

    fun getTasksByStatus(userId: Long, status: TaskStatus): Flow<List<TaskEntity>> {
        Log.d(TAG, "getTasksByStatus: fetching tasks for user $userId with status $status")
        return taskDao.getTasksByStatus(userId, status)
    }

    fun getOverdueTasks(userId: Long): Flow<List<TaskEntity>> {
        val currentTime = System.currentTimeMillis()
        Log.d(TAG, "getOverdueTasks: fetching overdue tasks for user $userId at $currentTime")
        return taskDao.getOverdueTasks(userId, currentTime)
    }

    fun getPendingTaskCount(userId: Long): Flow<Int> {
        Log.d(TAG, "getPendingTaskCount: fetching pending count for user $userId")
        return taskDao.getPendingTaskCount(userId)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        Log.d(TAG, "insertTask: inserting task ${task.title}")
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        Log.d(TAG, "updateTask: updating task ${task.id}")
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        Log.d(TAG, "deleteTask: deleting task ${task.id}")
        taskDao.deleteTask(task)
    }
}
