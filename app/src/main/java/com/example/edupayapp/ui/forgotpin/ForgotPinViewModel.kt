package com.example.edupayapp.ui.forgotpin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ForgotPinUiState(
    val phoneNumber: String = "",
    val error: String? = null
)

class ForgotPinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPinUiState())
    val uiState = _uiState.asStateFlow()

    fun onPhoneNumberChange(phone: String) {
        val error = if (phone.isNotEmpty() && !validateKenyanPhone(phone)) {
            "Please enter a valid Kenyan phone number"
        } else {
            null
        }
        _uiState.update { it.copy(phoneNumber = phone, error = error) }
    }

    fun onNextClicked() {
        //check if this number is registered -- to do ---
        // before proceeding to send an OTP.
        println("ViewModel: Requesting OTP for password reset.")
    }

    private fun validateKenyanPhone(phoneNumber: String): Boolean {
        val kenyanPhoneRegex = Regex("^(07|01)\\d{8}$|^(\\+254|254)(7|1)\\d{8}$")
        return kenyanPhoneRegex.matches(phoneNumber.replace("\\s".toRegex(), ""))
    }
}

