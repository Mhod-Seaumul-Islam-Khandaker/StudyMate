package com.example.studymate.ui.common

sealed interface UiState {
    data object Loading : UiState
    data class Success<T>(val data: T) : UiState
    data class Error(val message: String) : UiState
    data object Empty : UiState
}
