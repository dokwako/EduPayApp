package com.example.edupayapp.ui.otp

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.ui.common.OtpTextField

@Composable
fun OtpScreen(
    viewModel: OtpViewModel = viewModel(),
    onVerificationSuccess: () -> Unit
) {
    // This line will now work correctly because `collectAsState` is imported.
    val uiState by viewModel.uiState.collectAsState()

    // FIX 2: All references to properties must come from the `uiState` object.
    val isButtonEnabled = uiState.otpValue.length == 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Verify Your Number",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the 6-digit code sent to +254 7XX XXX XXX",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        OtpTextField(
            otpText = uiState.otpValue, // Use uiState.otpValue
            onOtpTextChange = { value, _ -> viewModel.onOtpChange(value) }
        )

        if (uiState.error != null) {
            Text(
                // Use uiState.error and the `!!` operator because it's inside a null check.
                text = uiState.error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Didn't receive code? Resend (45s)",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.onVerifyClicked()
                if (uiState.otpValue == "1256") {
                    onVerificationSuccess()
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
            Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}