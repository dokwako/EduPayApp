package com.example.edupayapp.ui.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignUpUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val idNumber: String = "",
    val phoneError: String? = null,
    val idNumberError: String? = null,
    val isLoading: Boolean = false
)

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPhoneNumberChange(phone: String) {
        val error = if (phone.isNotEmpty() && !validateKenyanPhone(phone)) {
            "Please enter a valid Kenyan phone number"
        } else {
            null
        }
        _uiState.update { it.copy(phoneNumber = phone, phoneError = error) }
    }

    fun onIdNumberChange(id: String) {
        val error = if (id.isNotEmpty() && !validateIdNumber(id)) {
            "Please enter a valid ID number"
        } else {
            null
        }
        _uiState.update { it.copy(idNumber = id, idNumberError = error) }
    }

    fun onSendOtpClicked() {
        println("ViewModel: Requesting OTP for user: ${_uiState.value}")
    }

    private fun validateKenyanPhone(phoneNumber: String): Boolean {
        val kenyanPhoneRegex = Regex("^(07|01)\\d{8}$|^(\\+254|254)(7|1)\\d{8}$")
        return kenyanPhoneRegex.matches(phoneNumber.replace("\\s".toRegex(), ""))
    }

    private fun validateIdNumber(idNumber: String): Boolean {
        return idNumber.length >= 7 && idNumber.all { it.isDigit() }
    }
}