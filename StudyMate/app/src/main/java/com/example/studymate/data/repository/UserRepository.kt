package com.example.studymate.data.repository

import android.util.Log
import com.example.studymate.data.local.dao.UserDao
import com.example.studymate.data.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    private val TAG = "UserRepository"

    suspend fun insertUser(user: UserEntity): Long {
        Log.d(TAG, "insertUser: creating user ${user.email}")
        return userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        Log.d(TAG, "getUserByEmail: searching for $email")
        return userDao.getUserByEmail(email)
    }
    
    fun getUserById(id: Long): Flow<UserEntity?> {
        Log.d(TAG, "getUserById: fetching user $id")
        return userDao.getUserById(id)
    }
    
    // Helper to get a default user for offline mode since we don't have real auth yet
    // In a real app, this would come from DataStore or SharedPreferences
    suspend fun getOrCreateDefaultUser(): Long {
        Log.d(TAG, "getOrCreateDefaultUser: Checking for default user")
        val defaultEmail = "student@studymate.com"
        val user = userDao.getUserByEmail(defaultEmail)
        return if (user != null) {
            Log.d(TAG, "getOrCreateDefaultUser: Found existing user ${user.id}")
            user.id
        } else {
            Log.d(TAG, "getOrCreateDefaultUser: Creating new default user")
            userDao.insertUser(UserEntity(name = "Student", email = defaultEmail, password = "password"))
        }
    }
}
