package com.example.edupayapp.ui.createpin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun CreatePinScreen(
    viewModel: CreatePinViewModel = viewModel(),
    onPinCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // The button is enabled only when both PINs are 4 digits long.
    val isButtonEnabled = uiState.pinValue.length == 4 && uiState.confirmedPinValue.length == 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Create Your PIN",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create a 4-digit PIN for future logins",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // First PIN input field reuse our common component.
        OtpTextField(
            otpText = uiState.pinValue,
            onOtpTextChange = { value, _ -> viewModel.onPinChange(value) },
            otpCount = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Second (confirmation) PIN input field.
        Text(text = "Confirm PIN", color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OtpTextField(
            otpText = uiState.confirmedPinValue,
            onOtpTextChange = { value, _ -> viewModel.onConfirmedPinChanges(value) },
            otpCount = 4
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
                viewModel.onFinishClicked()
                // Temporary navigation logic for the demo.
                val currentState = viewModel.uiState.value
                if (currentState.pinValue == currentState.confirmedPinValue && currentState.pinValue.length == 4) {
                    onPinCreated()
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
            Text("Finish", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}