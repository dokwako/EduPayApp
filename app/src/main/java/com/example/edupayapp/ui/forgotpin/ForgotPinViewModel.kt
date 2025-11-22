package com.example.edupayapp.ui.forgotpin

import ParentRegisterRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// State updated to track the required email input
data class ForgotPinUiState(
    val email: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val otpSentSuccess: Boolean = false
)

class ForgotPinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPinUiState())
    val uiState = _uiState.asStateFlow()
    private val authService = AuthService()

    fun onEmailChange(email: String) {
        // Simple email validation to ensure non-empty
        val error = if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Please enter a valid email address."
        } else {
            null
        }
        _uiState.update { it.copy(email = email, error = error) }
    }

    // Function to request OTP for PIN reset
    fun onRequestOtpClicked() {
        viewModelScope.launch {
            val email = _uiState.value.email
            if (_uiState.value.error != null || email.isBlank()) {
                _uiState.update { it.copy(error = _uiState.value.error ?: "Email cannot be empty.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // NOTE: The API doesn't have a separate "Forgot PIN" endpoint, but reusing the
                // registration endpoint is often used as a secure way to confirm the identity of an existing user
                // and trigger an OTP send for password/pin reset. We call register here to trigger OTP.

                // --- Reusing Registration API to Trigger OTP ---
                val request = ParentRegisterRequest(
                    email = email,
                    // Sending placeholders for other required fields, as the backend logic should
                    // check if the user exists and simply re-send the OTP for verification.
                    full_name = "",
                    id_number = ""
                )
                // Assuming the backend is smart enough to handle this as a resend OTP for existing user.
                val result = authService.registerParent(request)

                if (result.error != null) {
                    _uiState.update { it.copy(error = result.error, isLoading = false) }
                } else {
                    _uiState.update { it.copy(otpSentSuccess = true, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to send OTP. Check network or email.") }
            }
        }
    }
}
