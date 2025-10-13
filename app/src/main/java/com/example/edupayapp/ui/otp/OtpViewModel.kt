package com.example.edupayapp.ui.otp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OtpUiState(
    val otpValue : String = "",
    val error : String? = null,
    val isVerifying : Boolean = false
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
        val otp = _uiState.value.otpValue

        if (otp == "123456") { // We pretend "123456" is the correct code
            _uiState.update { it.copy(error = null) } // Clear any previous errors on success.
            // TODO: In the future, this will signal navigation to the UI.
            println("ViewModel: OTP Verification Successful!")
        } else {
            // If the OTP is wrong, we set an error message in the state. The UI will
            // automatically see this change and display the error text.
            _uiState.update { it.copy(error = "OTP Verification failed!") }
        }
    }
}