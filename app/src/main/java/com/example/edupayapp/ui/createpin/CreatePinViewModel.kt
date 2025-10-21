package com.example.edupayapp.ui.createpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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

    fun onPinChange(pin : String) {
       if(pin.length <= 4)  {
           _uiState.update { it.copy(pinValue = pin, error = null) }
       }
    }

    //called when 2nd confirmation pin input changes
    fun onConfirmedPinChanges(confirmPin : String) {
        if(confirmPin.length <= 4) {
            _uiState.update { it.copy(confirmedPinValue = confirmPin, error = null) }
        }
    }

    //called when submit button is clicked
    fun onFinishClicked() {
        viewModelScope.launch {
            val pin = _uiState.value.pinValue
            val confirmedPin = _uiState.value.confirmedPinValue

            if (pin != confirmedPin) {
                _uiState.update { it.copy(error = "PINs do not match. Please try again.") }
                return@launch
            }

            // Show loading state
            _uiState.update { it.copy(isCreating = true) }
            delay(2000) // Simulate network delay

            // Show success state
            _uiState.update { it.copy(isCreating = false, pinCreationSuccess = true) }
            println("ViewModel: PIN created successfully!")
        }
    }

}