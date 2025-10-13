package com.example.edupayapp.ui.register

// --- FIX 1: ADD ALL NECESSARY IMPORTS ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.R
import com.example.edupayapp.ui.common.InputField
import com.example.edupayapp.ui.common.SocialButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(),
    onSignUpSuccess: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    // --- FIX 2: THIS LINE IS NOW CORRECT ---
    // It correctly gets the state from the `viewModel` instance.
    val uiState by viewModel.uiState.collectAsState()

    // The validation check now reads from the `uiState` object.
    val isFormValid = uiState.name.isNotBlank() &&
            uiState.phoneNumber.isNotBlank() &&
            uiState.idNumber.isNotBlank() &&
            uiState.phoneError == null &&
            uiState.idNumberError == null

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
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )
        Text(
            text = "Welcome to EduPay",
            fontSize = 16.sp,
            color = Color(0xFF27272A)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- FIX 3: ALL REFERENCES TO uiState WILL NOW WORK ---
        InputField(
            label = "Name",
            value = uiState.name,
            onValueChange = { viewModel.onNameChange(it) },
            placeholder = "Enter your name"
        )

        Spacer(modifier = Modifier.height(20.dp))

        InputField(
            label = "Phone Number",
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onPhoneNumberChange(it) },
            placeholder = "Enter your phone number",
            keyboardType = KeyboardType.Phone,
            isError = uiState.phoneError != null
        )
        if (uiState.phoneError != null) {
            Text(
                text = uiState.phoneError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        InputField(
            label = "ID Number",
            value = uiState.idNumber,
            onValueChange = { viewModel.onIdNumberChange(it) },
            placeholder = "Enter your ID number",
            keyboardType = KeyboardType.Number,
            isError = uiState.idNumberError != null
        )
        if (uiState.idNumberError != null) {
            Text(
                text = uiState.idNumberError!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onSendOtpClicked()
                onSignUpSuccess()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6),
                disabledContainerColor = Color(0xFF7594D2)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Send OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))
            Text(" Or sign up with ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray)
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SocialButton(onClick = { /* TODO */ }, iconRes = R.drawable.ic_google)
            Spacer(modifier = Modifier.width(16.dp))
            SocialButton(onClick = { /* TODO */ }, iconRes = R.drawable.ic_facebook)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(
                text = "Already have an account? ",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = "Sign In",
                fontSize = 14.sp,
                color = Color(0xFF3B82F6),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }
}