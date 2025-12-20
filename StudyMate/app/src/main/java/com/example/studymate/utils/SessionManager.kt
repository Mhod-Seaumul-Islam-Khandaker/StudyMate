package com.example.studymate.utils

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studymate.auth.AuthActivity
import com.example.studymate.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SessionManager {
    // 1. Changed type from User to UserInfo
    private val _currentUser = MutableLiveData<UserInfo?>()
    val currentUser: LiveData<UserInfo?> = _currentUser

    init {
        // Load user session on initialization
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 2. Changed .gotrue to .auth
                val user = SupabaseClient.client.auth.currentUserOrNull()
                withContext(Dispatchers.Main) {
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun signOut() {
        try {
            // 3. Changed .gotrue.logout() to .auth.signOut()
            SupabaseClient.client.auth.signOut()
            _currentUser.postValue(null)
        } catch (e: Exception) {
            throw e
        }
    }

    // Check authentication status and redirect accordingly
    fun checkAuthAndRedirect(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // 4. Changed .gotrue to .auth
            val user = SupabaseClient.client.auth.currentUserOrNull()
            withContext(Dispatchers.Main) {
                val intent = if (user != null) {
                    Intent(context, MainActivity::class.java)
                } else {
                    Intent(context, AuthActivity::class.java)
                }
                // Ensure new task flags to prevent back-stack issues
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        }
    }
}
