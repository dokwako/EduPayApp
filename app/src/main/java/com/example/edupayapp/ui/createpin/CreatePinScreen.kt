package com.example.edupayapp.ui.createpin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.ui.common.OtpTextField
import kotlinx.coroutines.delay

@Composable
fun CreatePinScreen(
    viewModel: CreatePinViewModel = viewModel(),
    onPinCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // it waits 1 second, then calls the navigation function.
    if (uiState.pinCreationSuccess) {
        LaunchedEffect(Unit) {
            delay(1000)
            onPinCreated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // `when` decides which UI to show based on the ViewModel state.
        when {
            uiState.pinCreationSuccess -> {
                SuccessUI(message = "PIN set successfully!")
            }
            uiState.isCreating -> {
                LoadingUI(text = "Please hold on while we set up your account...")
            }
            else -> {
                CreatePinForm(
                    uiState = uiState,
                    onPinChange = { viewModel.onPinChange(it) },
                    onConfirmedPinChange = { viewModel.onConfirmedPinChanges(it) },
                    onFinishClicked = { viewModel.onFinishClicked() }
                )
            }
        }
    }
}

@Composable
private fun CreatePinForm(
    uiState: CreatePinUiState,
    onPinChange: (String) -> Unit,
    onConfirmedPinChange: (String) -> Unit,
    onFinishClicked: () -> Unit
) {
    val isButtonEnabled = uiState.pinValue.length == 4 && uiState.confirmedPinValue.length == 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(text = "Secure Your Account", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3D74E0))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Create a 4-digit PIN for quick logins", fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Enter your PIN", color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OtpTextField(
            otpText = uiState.pinValue,
            onOtpTextChange = { value, _ -> onPinChange(value) },
            otpCount = 4
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Confirm your PIN", color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OtpTextField(
            otpText = uiState.confirmedPinValue,
            onOtpTextChange = { value, _ -> onConfirmedPinChange(value) },
            otpCount = 4
        )
        if (uiState.error != null) {
            Text(text = uiState.error!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onFinishClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6), disabledContainerColor = Color(0xFFD1D5DB)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Set Pin", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingUI(text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = text, color = Color.Gray)
    }
}

@Composable
private fun SuccessUI(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF16A34A),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}