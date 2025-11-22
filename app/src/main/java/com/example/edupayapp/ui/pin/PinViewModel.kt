package com.example.edupayapp.ui.pin

import LoginRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.tokenStorage
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PinUiState(
    val pinValue: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false
)

class PinViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PinUiState())
    val uiState = _uiState.asStateFlow()
    private val authService = AuthService()

    // Retrieve email from storage for use in the login request
    val userEmail = tokenStorage.getEmail() ?: "Unknown User"

    // Simple way to extract name for the UI (e.g., "Welcome back, John")
    val displayUserName: String
        get() = userEmail.substringBefore('@').capitalize()

    fun onPinChange(pin: String) {
        // Max length is 6
        if (pin.length <= 6 && pin.all { it.isDigit() }) {
            _uiState.update { it.copy(pinValue = pin, error = null) }
        }
    }

    fun onLoginClicked() {
        val pin = _uiState.value.pinValue
        val email = tokenStorage.getEmail()

        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(error = "User session expired. Please log in again.", isLoading = false) }
            return
        }

        if (pin.length != 6) {
            _uiState.update { it.copy(error = "PIN must be 6 digits.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val request = LoginRequest(
                    email = email,
                    password_or_pin = pin
                )

                // Login call; token is saved in AuthService
                authService.login(request)

                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }

            } catch (e: IllegalStateException) {
                // Handle Invalid credentials (401)
                _uiState.update { it.copy(isLoading = false, error = "Incorrect PIN. Please try again.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Network error.") }
            }
        }
    }
}