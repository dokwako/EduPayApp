package com.example.edupayapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.ParentRegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val idNumber: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val signUpSuccess: Boolean = false
)

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // Use the new AuthService
    private val authService = AuthService()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onIdNumberChange(idNumber: String) {
        _uiState.update { it.copy(idNumber = idNumber, error = null) }
    }

    fun onRegisterClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Create request object for the API
                val request = ParentRegisterRequest(
                    email = _uiState.value.email,
                    full_name = _uiState.value.name,
                    id_number = _uiState.value.idNumber
                )

                // Call the API
                val result = authService.registerParent(request)

                if (result.error != null) {
                    // Handle API errors (e.g. "Email already exists")
                    _uiState.update { it.copy(error = result.error, isLoading = false) }
                } else {
                    // Success! Move to OTP
                    _uiState.update { it.copy(signUpSuccess = true, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Unknown error", isLoading = false) }
            }
        }
    }
}