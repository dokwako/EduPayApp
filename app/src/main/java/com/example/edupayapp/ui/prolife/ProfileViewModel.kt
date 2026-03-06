package com.example.edupayapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.tokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "",
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showLogoutDialog: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // 1. Initialize your real backend service
    private val authService = AuthService()

    init {
        fetchProfileData()
    }

    fun fetchProfileData() {
        // 2. Added Dispatchers.IO so the screen doesn't freeze!
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 3. Grab the real user ID from your login session
                val userId = tokenStorage.getUserId()

                if (userId != null) {
                    // 4. Ask the backend for the real user details
                    val dashboardData = authService.getDashboardSummary(userId)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = dashboardData.user.full_name // THE REAL NAME!
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "User not logged in.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load profile") }
            }
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