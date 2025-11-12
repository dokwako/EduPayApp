package com.example.edupayapp.ui.register

// Removed incorrect import: import android.R.attr.password
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.di.AppModule
import io.github.jan.supabase.gotrue.OtpType // Corrected import
import io.github.jan.supabase.gotrue.auth // Corrected import
import io.github.jan.supabase.gotrue.providers.builtin.Email // Added import
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val signUpSuccess: Boolean = false
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Corrected network call to supabase
                // Use 'signUpWith', specify the credentials type, and use OtpType.Email
                supabase.auth.signUpWith<Email.Credentials>(OtpType.Email) {
                    email = uiState.value.email
                    password = uiState.value.password
                }
                //on success,update the ui state
                _uiState.update { it.copy(signUpSuccess = true, isLoading = false) }
            } catch (e: Exception) {
                //on failure show error msg
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
