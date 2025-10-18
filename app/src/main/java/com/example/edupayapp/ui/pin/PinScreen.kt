package com.example.edupayapp.ui.pin

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.ui.common.OtpTextField

@Composable
fun PinScreen(
    viewModel: PinViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPinClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isButtonEnabled = uiState.pinValue.length == 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Enter Your PIN",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your 4-digit PIN to continue",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        OtpTextField(
            otpText = uiState.pinValue,
            onOtpTextChange = { value, _ -> viewModel.onPinChange(value) },
            otpCount = 4
        )

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Forgot PIN?",
            color = Color(0xFF3B82F6),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onForgotPinClicked() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.onLoginClicked()
                if (viewModel.uiState.value.pinValue == "1234") {
                    onLoginSuccess()
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
            Text("Log In", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}