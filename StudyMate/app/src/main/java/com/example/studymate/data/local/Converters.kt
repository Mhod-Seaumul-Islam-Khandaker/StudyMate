package com.example.studymate.data.local

import android.util.Log
import androidx.room.TypeConverter
import com.example.studymate.data.model.GoalStatus
import com.example.studymate.data.model.Priority
import com.example.studymate.data.model.TaskStatus
import com.example.studymate.data.model.TimerStatus

class Converters {
    private val TAG = "RoomConverters"

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        Log.d(TAG, "Converting TaskStatus $status to String")
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        Log.d(TAG, "Converting String $value to TaskStatus")
        return TaskStatus.valueOf(value)
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        Log.d(TAG, "Converting Priority $priority to String")
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        Log.d(TAG, "Converting String $value to Priority")
        return Priority.valueOf(value)
    }

    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String {
        Log.d(TAG, "Converting GoalStatus $status to String")
        return status.name
    }

    @TypeConverter
    fun toGoalStatus(value: String): GoalStatus {
        Log.d(TAG, "Converting String $value to GoalStatus")
        return GoalStatus.valueOf(value)
    }

    @TypeConverter
    fun fromTimerStatus(status: TimerStatus): String {
        Log.d(TAG, "Converting TimerStatus $status to String")
        return status.name
    }

    @TypeConverter
    fun toTimerStatus(value: String): TimerStatus {
        Log.d(TAG, "Converting String $value to TimerStatus")
        return TimerStatus.valueOf(value)
    }
}
