package com.example.studyapp.settings

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null
)