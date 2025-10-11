package com.example.edupayapp.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.edupayapp.R
import com.example.edupayapp.ui.register.InputField
import com.example.edupayapp.ui.register.SocialButton

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.showUnregisteredUserError) {
            UnregisteredUserUI(onSignUpClicked = onNavigateToSignUp)
        } else {
            val isButtonEnabled = uiState.phoneNumber.isNotBlank() && uiState.error == null

            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "Log in to EduPay",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D74E0)
            )

            Spacer(modifier = Modifier.height(40.dp))

            InputField(
                label = "Phone Number",
                value = uiState.phoneNumber,
                onValueChange = { viewModel.onPhoneNumberChange(it) },
                placeholder = "Enter your Phone Number",
                keyboardType = KeyboardType.Phone,
                isError = uiState.error != null
            )
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp).fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.onNextClicked()
                    // In a real app, navigation would be triggered by a state change from the VM
                    if (uiState.phoneNumber == "0712345678") onLoginSuccess()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFFD1D5DB)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // ... (Rest of your UI: Social buttons, Sign Up link, etc.)
        }
    }
}

// The UnregisteredUserUI composable does not need to change.