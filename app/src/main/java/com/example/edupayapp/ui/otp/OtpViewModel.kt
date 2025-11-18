package com.example.edupayapp.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.Supabase // FIX: Using your global Supabase object
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// This is your data class, it's perfect
data class OtpUiState(
    val otpValue: String = "",
    val error: String? = null,
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false
)

class OtpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState = _uiState.asStateFlow()

    // FIX: Get the Supabase client directly from your global object
    private val supabase = Supabase.client

    fun onOtpChange(otp: String) {
        // Only allow 6 digits
        if (otp.length <= 6 && otp.all { it.isDigit() }) {
            _uiState.update { it.copy(otpValue = otp, error = null) }
        }
    }

    // This is your verification logic, which is correct.
    // We just add a check to make sure the OTP is 6 digits.
    fun onVerifyClicked(email: String) {
        if (_uiState.value.otpValue.length != 6) {
            _uiState.update { it.copy(error = "OTP must be 6 digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, error = null) }
            try {
                // This is the real Supabase call
                supabase.auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP,
                    email = email,
                    token = _uiState.value.otpValue
                )

                // On success, update state to trigger navigation
                _uiState.update { it.copy(isVerifying = false, verificationSuccess = true) }

            } catch (e: Exception) {
                // On failure, show the error
                _uiState.update { it.copy(isVerifying = false, error = e.message) }
            }
        }
    }
}