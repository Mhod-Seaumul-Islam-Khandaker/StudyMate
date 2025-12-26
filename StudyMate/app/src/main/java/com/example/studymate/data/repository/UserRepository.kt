package com.example.studymate.data.repository

import android.content.Context
import android.util.Log
import com.example.studymate.data.local.dao.UserDao
import com.example.studymate.data.model.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) {
    private val TAG = "UserRepository"
    private val PREFS_NAME = "studymate_prefs"
    private val KEY_USER_ID = "logged_in_user_id"

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

    fun saveUserSession(userId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
        Log.d(TAG, "saveUserSession: Saved user $userId")
    }

    fun getLoggedInUserId(): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getLong(KEY_USER_ID, -1L)
        Log.d(TAG, "getLoggedInUserId: $id")
        return id
    }

    fun logout() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        Log.d(TAG, "logout: Session cleared")
    }
    
    // Kept for compatibility if referenced elsewhere, but now delegates to real session or default
    suspend fun getOrCreateDefaultUser(): Long {
        val loggedInId = getLoggedInUserId()
        if (loggedInId != -1L) return loggedInId

        Log.d(TAG, "getOrCreateDefaultUser: No logged in user, creating/fetching default")
        val defaultEmail = "student@studymate.com"
        val user = userDao.getUserByEmail(defaultEmail)
        val id = if (user != null) {
            user.id
        } else {
            userDao.insertUser(UserEntity(name = "Student", email = defaultEmail, password = "password"))
        }
        saveUserSession(id) // Auto-login default user if this method is called (fallback)
        return id
    }
}
