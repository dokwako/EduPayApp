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
    val isErrorState = uiState.error != null

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "Log In to EduPay",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Text(
            text = "Welcome back!",
            fontSize = 16.sp,
            color = Color(0xFF27272A)
        )
        Spacer(modifier = Modifier.height(40.dp))

        InputField(
            label = "Email Address",
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "Enter your Email Address",
            keyboardType = KeyboardType.Email,
            isError = isErrorState
        )

        Spacer(modifier = Modifier.height(20.dp))

        InputField(
            label = "Password / PIN",
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "Enter your Password or PIN",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            isError = isErrorState
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
                disabledContainerColor = Color(0xFF7594D2)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = "Sign Up",
                fontSize = 14.sp,
                color = Color(0xFF3B82F6),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )
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
        Icon(
            painter = painterResource(id = R.drawable.ic_error_warning),
            contentDescription = "Error Warning",
            tint = Color(0xFFDC2626),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No account found with this email. Would you like to Sign Up?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color(0xFF27272A),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSignUpClicked,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}