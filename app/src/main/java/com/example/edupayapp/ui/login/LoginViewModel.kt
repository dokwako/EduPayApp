package com.example.edupayapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.supabase.AppModule
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.providers.builtin.Email

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val showUnregisteredUserError: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val supabase = AppModule.supabase

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null, showUnregisteredUserError = false) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null, showUnregisteredUserError = false) }
    }

    // NEW: Replaced fake logic with REAL Supabase call
    fun onLoginClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // This is the real network call
                supabase.auth.signInWith(Email) {
                    this.email = _uiState.value.email
                    this.password = _uiState.value.password
                }
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }

            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred"
                if (errorMessage.contains("Invalid login credentials")) {
                    _uiState.update { it.copy(isLoading = false, showUnregisteredUserError = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                }
            }
        }
    }
}