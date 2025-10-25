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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
            LoginFormUI(
                uiState = uiState,
                onPhoneNumberChange = { viewModel.onPhoneNumberChange(it) },
                onNextClicked = {
                    viewModel.onNextClicked()
                    if (viewModel.uiState.value.phoneNumber == "0712345678") {
                        onLoginSuccess()
                    }
                },
                onNavigateToSignUp = onNavigateToSignUp
            )
        }
    }
}

@Composable
private fun LoginFormUI(
    uiState: LoginUiState,
    onPhoneNumberChange: (String) -> Unit,
    onNextClicked: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val isButtonEnabled = uiState.phoneNumber.isNotBlank() && uiState.error == null

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Log In to EduPay",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )

        Spacer(modifier = Modifier.height(40.dp))

        InputField(
            label = "Phone Number",
            value = uiState.phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = "Enter your Phone Number",
            keyboardType = KeyboardType.Phone,
            isError = uiState.error != null
        )
        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNextClicked,
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
            Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(" Or log in with ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SocialButton(onClick = { /* TODO: Google Login */ }, iconRes = R.drawable.ic_google)
            Spacer(modifier = Modifier.width(16.dp))
           // SocialButton(onClick = { /* TODO: Facebook Login */ }, iconRes = R.drawable.ic_facebook)
        }

        // This Spacer will now work correctly.
        Spacer(modifier = Modifier.weight(1f))

        Row {
            Text(
                text = "Don't have an account? ",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = "Sign up",
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
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No account found with this number. Would you like to Sign Up?",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSignUpClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}