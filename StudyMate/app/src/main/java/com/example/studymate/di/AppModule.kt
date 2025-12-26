package com.example.studymate.di

import android.content.Context
import androidx.room.Room
import com.example.studymate.data.local.StudyDatabase
import com.example.studymate.data.local.dao.GoalDao
import com.example.studymate.data.local.dao.TaskDao
import com.example.studymate.data.local.dao.TimerDao
import com.example.studymate.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStudyDatabase(@ApplicationContext context: Context): StudyDatabase {
        return Room.databaseBuilder(
            context,
            StudyDatabase::class.java,
            "studymate_db"
        ).fallbackToDestructiveMigration() // For development simplicity
         .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: StudyDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideTaskDao(db: StudyDatabase): TaskDao = db.taskDao()

    @Provides
    @Singleton
    fun provideGoalDao(db: StudyDatabase): GoalDao = db.goalDao()

    @Provides
    @Singleton
    fun provideTimerDao(db: StudyDatabase): TimerDao = db.timerDao()
}
