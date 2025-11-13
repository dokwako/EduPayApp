package com.example.edupayapp.ui.forgotpin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import com.example.edupayapp.ui.common.InputField

@Composable
fun ForgotPinScreen(
    viewModel: ForgotPinViewModel = viewModel(),
    onNavigateToOtp: (email: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isButtonEnabled = uiState.phoneNumber.isNotBlank() && uiState.error == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Reset Your PIN",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your registered phone number to receive a verification code.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
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
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.onNextClicked()
                if (isButtonEnabled) {
                    onNavigateToOtp(uiState.phoneNumber)
                }
            },
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
            Text("Send Code", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}