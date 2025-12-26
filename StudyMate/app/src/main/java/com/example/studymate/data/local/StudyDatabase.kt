package com.example.studymate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studymate.data.local.dao.GoalDao
import com.example.studymate.data.local.dao.TaskDao
import com.example.studymate.data.local.dao.TimerDao
import com.example.studymate.data.local.dao.UserDao
import com.example.studymate.data.model.GoalEntity
import com.example.studymate.data.model.NotificationEntity
import com.example.studymate.data.model.TaskEntity
import com.example.studymate.data.model.TimerEntity
import com.example.studymate.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        GoalEntity::class,
        TimerEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
    abstract fun timerDao(): TimerDao
}
