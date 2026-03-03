package com.example.edupayapp.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.DashboardSummaryResponse
import com.example.edupayapp.data.tokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val summary: DashboardSummaryResponse? = null
)

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val authService = AuthService()

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val parentId = tokenStorage.getUserId()

        if (parentId.isNullOrBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "User ID missing. Please log in.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val data = authService.getDashboardSummary(parentId)
                _uiState.update { it.copy(isLoading = false, summary = data) }
            } catch (e: IllegalStateException) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load dashboard.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Network Error: Could not connect to API.") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authService.logout()
        }
    }
}