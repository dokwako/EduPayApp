package com.example.edupayapp.ui.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
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
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    email: String, // The email is passed in, which is perfect
    viewModel: OtpViewModel = viewModel(),
    onVerificationSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // When verificationSuccess becomes true, this triggers.
    if (uiState.verificationSuccess) {
        LaunchedEffect(Unit) {
            delay(1000) // Wait 1 second to show success
            onVerificationSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Your UI state handling is excellent
        when {
            uiState.verificationSuccess -> {
                SuccessUI(message = "Verification successful!")
            }
            uiState.isVerifying -> {
                LoadingUI(text = "Verifying...")
            }
            else -> {
                // Pass the email down to the input form
                OtpInputForm(
                    email = email,
                    uiState = uiState,
                    onOtpChange = { viewModel.onOtpChange(it) },
                    onVerifyClicked = { viewModel.onVerifyClicked(email) }
                )
            }
        }
    }
}

@Composable
private fun OtpInputForm(
    email: String, // Receive the email to display it
    uiState: OtpUiState,
    onOtpChange: (String) -> Unit,
    onVerifyClicked: () -> Unit
) {
    val isButtonEnabled = uiState.otpValue.length == 6 && !uiState.isVerifying

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            // FIX: Changed text to "Email"
            text = "Verify Your Email",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            // FIX: Changed text to show the user's email
            text = "Enter the 6-digit code sent to $email",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))

        // FIX: Replaced custom OtpTextField with a standard one
        OutlinedTextField(
            value = uiState.otpValue,
            onValueChange = onOtpChange,
            label = { Text("6-Digit Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = uiState.error != null // Show error state
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error, // Use theme color for error
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Didn't receive code? Resend", // TODO: Add resend logic later
            color = Color.Gray
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onVerifyClicked,
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
            Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingUI(text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text, color = Color.Gray)
    }
}

@Composable
private fun SuccessUI(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF16A34A),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}