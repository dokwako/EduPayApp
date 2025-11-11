package com.example.edupayapp.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.R
import com.example.edupayapp.ui.common.InputField
import com.example.edupayapp.ui.common.SocialButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.showUnregisteredUserError) {
                UnregisteredUserUI(onSignUpClicked = onNavigateToSignUp)
            } else {
                LoginFormUI(
                    uiState = uiState,
                    onEmailChange = { viewModel.onEmailChange(it) },
                    onPasswordChange = { viewModel.onPasswordChange(it) },
                    onLoginClicked = { viewModel.onLoginClicked() },
                    onNavigateToSignUp = onNavigateToSignUp
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun LoginFormUI(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val isButtonEnabled = uiState.email.isNotBlank() && uiState.password.isNotBlank() && !uiState.isLoading

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(text = "Log In to EduPay", /*...styling...*/ )
        Spacer(modifier = Modifier.height(40.dp))

        // --- UI CHANGES ---
        InputField(
            label = "Email Address",
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "Enter your Email Address",
            keyboardType = KeyboardType.Email,
            isError = uiState.error != null || uiState.showUnregisteredUserError
        )

        Spacer(modifier = Modifier.height(20.dp))

        InputField(
            label = "Password",
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "Enter your Password",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.error != null || uiState.showUnregisteredUserError
        )

        if (uiState.error != null) {
            Text(text = uiState.error!!, color = Color.Red, /*...*/ )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isButtonEnabled,
            /*...styling...*/
        ) {
            Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

    }
}

@Composable
private fun UnregisteredUserUI(onSignUpClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_error_warning) )
        Spacer(modifier = Modifier.height(24.dp))
        // NEW: Updated error text
        Text(
            text = "No account found with this email. Would you like to Sign Up?",
            /*...styling...*/
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSignUpClicked,
            /*...styling...*/
        ) {
            Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}