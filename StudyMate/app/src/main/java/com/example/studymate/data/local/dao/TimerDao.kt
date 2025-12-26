package com.example.studymate.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studymate.data.model.TimerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer: TimerEntity): Long

    @Update
    suspend fun updateTimer(timer: TimerEntity)

    @Query("SELECT * FROM timers WHERE userId = :userId LIMIT 1")
    fun getTimerForUser(userId: Long): Flow<TimerEntity?>

    @Query("DELETE FROM timers WHERE userId = :userId")
    suspend fun deleteTimerForUser(userId: Long)
}
