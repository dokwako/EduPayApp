package com.example.edupayapp.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.OtpVerificationRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OtpUiState(
    val otpValue: String = "",
    val error: String? = null,
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false
)

class OtpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState = _uiState.asStateFlow()

    private val authService = AuthService()

    fun onOtpChange(otp: String) {
        if (otp.length <= 6 && otp.all { it.isDigit() }) {
            _uiState.update { it.copy(otpValue = otp, error = null) }
        }
    }

    fun onVerifyClicked(email: String) {
        if (_uiState.value.otpValue.length != 6) {
            _uiState.update { it.copy(error = "OTP must be 6 digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, error = null) }
            try {
                val request = OtpVerificationRequest(
                    email = email,
                    otp = _uiState.value.otpValue
                )

                val result = authService.verifyOtp(request)

                if (result.error != null) {
                    _uiState.update { it.copy(isVerifying = false, error = result.error) }
                } else {
                    // OTP Verified!
                    _uiState.update { it.copy(isVerifying = false, verificationSuccess = true) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isVerifying = false, error = e.message) }
            }
        }
    }
}