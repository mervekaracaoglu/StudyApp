package com.example.studyapp.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        viewModelScope.launch {
            SettingsDataStore.getSettings(context).collect { prefs ->
                val user = auth.currentUser
                _uiState.value = prefs.copy(
                    isLoggedIn = user != null,
                    userEmail = user?.email
                )
            }
        }
    }

    fun toggleTheme() {
        val newTheme = !_uiState.value.isDarkTheme
        saveTheme(newTheme)
    }

    private fun saveTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            SettingsDataStore.saveThemePreference(context, isDarkTheme)
        }
    }
}