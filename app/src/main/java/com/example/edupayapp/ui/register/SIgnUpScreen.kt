package com.example.edupayapp.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edupayapp.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }

    //Checks all the fields
    val isFormValid = name.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            idNumber.isNotBlank() &&
            phoneError == null

    //check phone number matches kenyan
    fun validateKenyanPhone(phoneNumber: String): Boolean {
        val kenyanPhoneRegex = Regex("^(07|01)\\d{8}$|^(\\+254|254)(7|1)\\d{8}$")

        // Remove any characters from the phone number
        return kenyanPhoneRegex.matches(phoneNumber.replace("\\s".toRegex(), ""))
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer( modifier = Modifier.height(120.dp))

        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D74E0)
        )

        Spacer( modifier = Modifier.height(8.dp))

        //Subtitle
        Text(
            text = "Welcome to EduPay",
            fontSize = 16.sp,
            color = Color(0xFF27272A)
        )

        Spacer( modifier = Modifier.height(40.dp))

        //input fields
        InputField(
            label = "Name",
            value = name,
            onValueChange = { name = it },
            placeholder = "Enter your name"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column {
            InputField(
                label = "Phone Number",
                value = phoneNumber,
                onValueChange = { newValue ->
                    phoneNumber = newValue // Update the phoneNumber

                    //checks validity
                    phoneError = if (newValue.isNotEmpty() && !validateKenyanPhone(newValue)) {
                        "Please enter a valid Kenyan phone number"
                    } else {
                        null
                    }
                },
                placeholder = "Enter your phone number",
                keyboardType = KeyboardType.Phone,
                isError = phoneError != null
            )

            //display phone error if invalid
            if (phoneError != null) {
                Text(
                    text = phoneError!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //id number input
        InputField(
            label = "ID Number",
            value = idNumber,
            onValueChange = { idNumber = it },
            placeholder = "Enter your ID number",
            keyboardType = KeyboardType.Number
        )

        Spacer( modifier = Modifier.height(24.dp))

        //send otp
        Button(
            onClick = {
                if (isFormValid) {
                    //send data to backend api ,request otp to send to number
                    onSignUpSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = isFormValid,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3B82F6), // blue when enabled
                disabledContainerColor = Color(0xFF7594D2), // gray when disabled
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Send OTP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer( modifier = Modifier.height(24.dp))

        // divider with sign up
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF6B7280)
            )
        }

        Spacer( modifier = Modifier.height(24.dp))

        //social login buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Google button
            SocialButton(
                onClick = {
                    // TODO: Implement Google Sign In
                },
                iconRes = R.drawable.ic_google
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Facebook button
            SocialButton(
                onClick = {
                    // TODO: Implement Facebook Sign In
                },
                iconRes = R.drawable.ic_facebook
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SIGN IN LINK
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
                modifier = Modifier.clickable {
                    onSignInClick()  // Navigate to login screen when clicked
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SocialButton(
    onClick: () -> Unit,
    iconRes: Int
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color(0xFFE5E7EB), CircleShape)
            .background(Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Display the icon image
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF1A237E)) },
        placeholder = { Text(placeholder, color = Color.Gray) },
        singleLine = true,
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),

        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color(0xFF3949AB),
            unfocusedIndicatorColor = Color(0xFF9FA8DA),
            cursorColor = Color(0xFF1A237E),
            focusedLabelColor = Color(0xFF1A237E),
            unfocusedLabelColor = Color(0xFF303F9F),
            errorIndicatorColor = Color.Red,
            errorCursorColor = Color.Red
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
