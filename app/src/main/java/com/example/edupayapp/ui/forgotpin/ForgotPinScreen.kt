package com.example.edupayapp.ui.forgotpin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.ui.common.InputField

@Composable
fun ForgotPinScreen(
    viewModel: ForgotPinViewModel = viewModel(),
    onNavigateToOtp: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit // <-- NEW: Callback to go back to Login
) {
    val uiState by viewModel.uiState.collectAsState()

    val isButtonEnabled = uiState.email.isNotBlank() && uiState.error == null && !uiState.isLoading

    // Triggers navigation after OTP is successfully requested from ViewModel
    LaunchedEffect(uiState.otpSentSuccess) {
        if (uiState.otpSentSuccess) {
            onNavigateToOtp(uiState.email)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Forgot PIN?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your registered email address to receive a verification code.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        InputField(
            label = "Email Address",
            value = uiState.email,
            onValueChange = { viewModel.onEmailChange(it) },
            placeholder = "Enter your Email Address",
            keyboardType = KeyboardType.Email,
            isError = uiState.error != null
        )
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CLICKABLE IMPLEMENTED HERE ---
        Text(
            text = "Remember your PIN? Log In",
            color = Color.Gray,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { onNavigateToLogin() } // <-- Call the new navigation function
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.onRequestOtpClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
                disabledContainerColor = Color(0xFFD1D5DB)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

