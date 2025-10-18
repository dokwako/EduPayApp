package com.example.edupayapp.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OtpUiState(
    val otpValue : String = "",
    val error : String? = null,
    val isVerifying : Boolean = false,
    val verificationSuccess: Boolean = false
)

class OtpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtpUiState())

    val uiState = _uiState.asStateFlow()

    fun onOtpChange(otp : String) {
        if (otp.length <= 6) {
            // validate otp
            _uiState.update { it.copy(otpValue = otp, error = null) }
        }
    }

    // verify otp
    fun onVerifyClicked() {
        viewModelScope.launch {
            // Show the "Verifying..." loading state.
            _uiState.update { it.copy(isVerifying = true) }
            delay(1500)

            val otp = _uiState.value.otpValue
            if (otp == "123456") {
                //  On success, show the "Success!" state.
                _uiState.update { it.copy(isVerifying = false, verificationSuccess = true) }
            } else {
                // On failure, stop loading and show the error.
                _uiState.update {
                    it.copy(isVerifying = false, error = "OTP Verification failed!")
                }
            }
        }
    }
}
