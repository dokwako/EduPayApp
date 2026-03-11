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
}