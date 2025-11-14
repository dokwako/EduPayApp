package com.example.edupayapp.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    onSignUpSuccess: (String) -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Updated validation logic for Email/Password
    val isFormValid = uiState.email.isNotBlank() &&
            uiState.password.isNotBlank()

    // This listens for the `signUpSuccess` flag from the ViewModel.
    // When it becomes true, it triggers the navigation.
    LaunchedEffect(uiState.signUpSuccess) {
        if (uiState.signUpSuccess) {
            onSignUpSuccess(uiState.email)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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

//            InputField(
//                label = "Name",
//                value = uiState.name,
//                onValueChange = { viewModel.onEmailChange(it) },
//                placeholder = "Enter your Name",
//                keyboardType = KeyboardType.Text
//            )
            InputField(
                label = "Email",
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputField(
                label = "Password",
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                placeholder = "Enter your password",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onSignUpClicked() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = isFormValid && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF7594D2)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
               // SocialButton(onClick = { /* TODO */ }, iconRes = R.drawable.ic_google)
                //SocialButton(onClick = { /* TODO */ }, iconRes = R.drawable.ic_facebook)
            }



            Spacer(modifier = Modifier.height(24.dp))

            Row {
                Text(text = "Already have an account? ", fontSize = 14.sp, color = Color(0xFF6B7280))
                Text(
                    text = "Sign In",
                    fontSize = 14.sp,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }
        }

        // Show a loading spinner over the screen
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}