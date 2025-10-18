package com.example.edupayapp.ui.pin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PinUiState(
    val pinValue: String = "",
    val error: String? = null,
    val isLoading: Boolean = false
)

class PinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PinUiState())
    val uiState = _uiState.asStateFlow()

    fun onPinChange(pin: String) {
        if (pin.length <= 4) {
            _uiState.update { it.copy(pinValue = pin, error = null) }
        }
    }

    fun onLoginClicked() {
        val pin = _uiState.value.pinValue
        // --- FAKE BACKEND CHECK FOR DEMO ---
        if (pin == "1234") { // Correct PIN
            _uiState.update { it.copy(error = null) }
            println("ViewModel: PIN Login Successful!")
        } else {
            _uiState.update { it.copy(error = "Incorrect PIN. Please try again.") }
        }
    }
}