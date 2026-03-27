package com.example.edupayapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.DashboardSummaryResponse
import com.example.edupayapp.data.tokenStorage
import kotlinx.coroutines.delay // ADDED: For the polling timer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive // ADDED: To safely run the loop
import kotlinx.coroutines.launch

// 1. UPDATED STATE: Added the notification count variable
data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val summary: DashboardSummaryResponse? = null,
    val unreadNotificationCount: Int = 0 // The UI is now listening for this
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState
    private val authService = AuthService()

    init {
        fetchDashboardData()
        startNotificationPolling() // 2. Start the engine when the screen opens
    }

    fun fetchDashboardData() {
        val parentId = tokenStorage.getUserId()
        if (parentId.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Login session expired.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val data = authService.getDashboardSummary(parentId)
                _uiState.update { it.copy(isLoading = false, summary = data) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Could not fetch dashboard data.") }
            }
        }
    }

    // 3. THE POLLING ENGINE
    private fun startNotificationPolling() {
        viewModelScope.launch {
            // isActive ensures the loop stops cleanly if the ViewModel is destroyed
            while (isActive) {
                try {
                    // Call the backend for the fresh count
                    val response = authService.getUnreadNotificationCount()

                    // Update the UI state instantly
                    _uiState.update { it.copy(unreadNotificationCount = response.unread_count) }
                } catch (e: Exception) {
                    // Silently ignore errors (e.g., brief loss of internet)
                    // We don't want error popups disrupting the user for a background task.
                }

                // Wait for 2 minutes (120,000 milliseconds) before asking again
                delay(120_000L)
            }
        }
    }
}