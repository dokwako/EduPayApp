package com.example.edupayapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showLogoutDialog: Boolean = false // To control the logout pop-up
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfileData()
    }

    fun fetchProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(1000) // Simulate network delay
            // On success:
            _uiState.update { it.copy(isLoading = false) }
            // To test error state, uncomment the line below:
            // _uiState.update { it.copy(isLoading = false, error = "Failed to load profile") }
        }
    }

    fun onNotificationToggle(isEnabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = isEnabled) }
    }

    fun onLogoutClicked() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun onDismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }
}