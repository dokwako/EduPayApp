package com.example.edupayapp.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.supabase.AppModule
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
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

    // Get the Supabase client
    private val supabase = AppModule.supabase

    fun onOtpChange(otp: String) {
        if (otp.length <= 6) {
            _uiState.update { it.copy(otpValue = otp, error = null) }
        }
    }

    // --- THIS IS THE CORRECTED FUNCTION ---
    // It now accepts the user's email and makes a real backend call.
    fun onVerifyClicked(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, error = null) }
            try {
                // This is the REAL network call to Supabase
                supabase.auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP, // Tell Supabase this is a sign-up OTP
                    email = email,
                    token = _uiState.value.otpValue
                )

                // On success, update state
                _uiState.update { it.copy(isVerifying = false, verificationSuccess = true) }

            } catch (e: Exception) {
                // On failure, show the real error from Supabase
                _uiState.update { it.copy(isVerifying = false, error = e.message) }
            }
        }
    }
}