package com.example.edupayapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.Supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val idNumber: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val signUpSuccess: Boolean = false
)

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // We get the client directly from your Supabase object
    private val supabase = Supabase.client

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onIdNumberChange(idNumber: String) {
        _uiState.update { it.copy(idNumber = idNumber, error = null) }
    }

    fun onSendOtpClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supabase.auth.signUpWith(OTP) {
                    email = _uiState.value.email

                    // We send the extra data here
                    data = buildJsonObject {
                        put("full_name", _uiState.value.name)
                        put("id_number", _uiState.value.idNumber)
                    }
                }
                _uiState.update { it.copy(signUpSuccess = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}