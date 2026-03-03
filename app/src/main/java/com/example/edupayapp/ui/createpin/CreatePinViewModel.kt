package com.example.edupayapp.ui.createpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.PinSetupRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreatePinUiState(
    val pinValue : String = "",
    val confirmedPinValue : String = "",
    val error : String? = null,
    val isCreating: Boolean = false,
    val pinCreationSuccess: Boolean = false
)

class CreatePinViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreatePinUiState())
    val uiState = _uiState.asStateFlow()
    private val authService = AuthService()

    private var userEmail: String = ""

    fun initialize(email: String) {
        userEmail = email
    }

    fun onPinChange(pin : String) {
        // FIX: Max length is 6
        if(pin.length <= 6 && pin.all { it.isDigit() })  {
            _uiState.update { it.copy(pinValue = pin, error = null) }
        }
    }

    fun onConfirmedPinChanges(confirmPin : String) {
        // FIX: Max length is 6
        if(confirmPin.length <= 6 && confirmPin.all { it.isDigit() }) {
            _uiState.update { it.copy(confirmedPinValue = confirmPin, error = null) }
        }
    }

    fun onFinishClicked() {
        viewModelScope.launch {
            val pin = _uiState.value.pinValue
            val confirmedPin = _uiState.value.confirmedPinValue
            val email = userEmail

            if (email.isBlank()) {
                _uiState.update { it.copy(error = "Error: Email missing for PIN setup.", isCreating = false) }
                return@launch
            }

            if (pin != confirmedPin) {
                _uiState.update { it.copy(error = "PINs do not match. Please try again.") }
                return@launch
            }

            // FIX: Check for 6-digit length (Mandatory security requirement)
            if (pin.length != 6) {
                _uiState.update { it.copy(error = "PIN must be 6 digits.") }
                return@launch
            }

            _uiState.update { it.copy(isCreating = true, error = null) }

            try {
                // API Call: Final step of registration
                val request = PinSetupRequest(email = email, pin = pin)
                val result = authService.setParentPin(request)

                if (result.error != null) {
                    _uiState.update { it.copy(error = result.error, isCreating = false) }
                } else {
                    // Success!
                    _uiState.update { it.copy(isCreating = false, pinCreationSuccess = true) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Network error during PIN setup.", isCreating = false) }
            }
        }
    }
}