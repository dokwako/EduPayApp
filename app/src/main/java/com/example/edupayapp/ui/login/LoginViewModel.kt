package com.example.edupayapp.ui.login

import LoginRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "", // This holds the user's PIN/Password
    val error: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val showUnregisteredUserError: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val authService = AuthService()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null, showUnregisteredUserError = false) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null, showUnregisteredUserError = false) }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showUnregisteredUserError = false) }
            try {
                // Ensure fields are not empty before calling the API
                if (_uiState.value.email.isBlank() || _uiState.value.password.isBlank()) {
                    _uiState.update { it.copy(isLoading = false, error = "Email and PIN/Password are required.") }
                    return@launch
                }

                val request = LoginRequest(
                    email = _uiState.value.email,
                    password_or_pin = _uiState.value.password
                )

                // Call the Ktor login function
                authService.login(request)

                // Login successful
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }

            } catch (e: IllegalStateException) {
                // Handle Invalid credentials (401) and custom errors from AuthService
                _uiState.update { it.copy(isLoading = false, error = null) }
                if (e.message?.contains("Invalid credentials") == true) {
                    _uiState.update { it.copy(showUnregisteredUserError = true) } // Triggers the 'Log In failed' UI
                } else {
                    _uiState.update { it.copy(error = e.message ?: "Authentication failed.") }
                }
            } catch (e: Exception) {
                // Handle general network errors (which includes the detailed message from the AuthService fix)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Network error occurred") }
            }
        }
    }
}