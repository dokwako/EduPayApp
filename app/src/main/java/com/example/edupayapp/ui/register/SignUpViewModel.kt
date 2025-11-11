package com.example.edupayapp.ui.register

import androidx.lifecycle.ViewModel
import com.example.edupayapp.di.AppModule
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val SignUpsuccess : Boolean = false
)

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    //getting the supabase client from AppModule
    private val supabase = AppModule.supabase

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onSignUpClicked() {
        ViewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                //network call to supabase
                supabase.auth.signUp(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
                //on success,update the ui state
                _uiState.update { it.copy(SignUpsuccess = true, isLoading = false) }
            } catch (e: Exception) {
                //on failure show error msg
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }


}