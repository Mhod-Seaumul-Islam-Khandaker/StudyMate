package com.example.studymate.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.model.UserEntity
import com.example.studymate.data.repository.UserRepository
import com.example.studymate.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val TAG = "AuthViewModel"

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    if (user.password == pass) {
                        Log.d(TAG, "login: Success for ${user.email}")
                        userRepository.saveUserSession(user.id)
                        _uiState.value = UiState.Success("Login Successful")
                    } else {
                        Log.w(TAG, "login: Incorrect password")
                        _uiState.value = UiState.Error("Invalid email or password")
                    }
                } else {
                    Log.w(TAG, "login: User not found")
                    _uiState.value = UiState.Error("User not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "login: Error", e)
                _uiState.value = UiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signup(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    Log.w(TAG, "signup: Email already exists")
                    _uiState.value = UiState.Error("Email already registered")
                    return@launch
                }

                val newUser = UserEntity(name = name, email = email, password = pass)
                val id = userRepository.insertUser(newUser)
                Log.d(TAG, "signup: Created user $id")
                userRepository.saveUserSession(id)
                _uiState.value = UiState.Success("Account created")
            } catch (e: Exception) {
                Log.e(TAG, "signup: Error", e)
                _uiState.value = UiState.Error(e.message ?: "Signup failed")
            }
        }
    }
}
