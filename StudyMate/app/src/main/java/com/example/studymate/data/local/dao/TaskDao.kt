package com.example.studymate.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studymate.data.model.TaskEntity
import com.example.studymate.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY dueDate ASC")
    fun getTasksForUser(userId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status = :status ORDER BY dueDate ASC")
    fun getTasksByStatus(userId: Long, status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND dueDate < :currentTimeMillis AND status != 'COMPLETED'")
    fun getOverdueTasks(userId: Long, currentTimeMillis: Long): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = 'PENDING'")
    fun getPendingTaskCount(userId: Long): Flow<Int>
}
