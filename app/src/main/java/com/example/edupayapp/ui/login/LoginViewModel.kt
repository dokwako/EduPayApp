package com.example.edupayapp.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val phoneNumber: String = "",
    val error: String? = null,
    val showUnregisteredUserError: Boolean = false,
    val isLoading: Boolean = false // For future use with loading spinners
)


class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState = _uiState.asStateFlow()

    fun onPhoneNumberChange(phone: String) {
        val error = if (phone.isNotEmpty() && !validateKenyanPhone(phone)) {
            "Please enter a valid Kenyan phone number"
        } else {
            null
        }
        // `update` safely creates a new copy of the state with the changes.
        _uiState.update { currentState ->
            currentState.copy(
                phoneNumber = phone,
                error = error,
                // When the user starts typing again, we hide the "unregistered" error.
                showUnregisteredUserError = false
            )
        }
    }

    fun onNextClicked() {
        val currentPhoneNumber = _uiState.value.phoneNumber
        // --- FAKE BACKEND CHECK FOR DEMO ---
        // In a real app, this is where you'd call your repository to check the number.
        if (currentPhoneNumber == "0712345678") {
            // TODO: Signal navigation to the PIN screen.
            println("ViewModel: Login successful for $currentPhoneNumber")
        } else {
            _uiState.update { it.copy(showUnregisteredUserError = true) }
        }
    }

    private fun validateKenyanPhone(phone: String): Boolean {
        val kenyanPhoneRegex = Regex("^(07|01)\\d{8}$|^(\\+254|254)(7|1)\\d{8}$")
        return kenyanPhoneRegex.matches(phone.replace("\\s".toRegex(), ""))
    }
}