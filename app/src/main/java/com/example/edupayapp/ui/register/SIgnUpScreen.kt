package com.example.edupayapp.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
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
        Spacer( modifier = Modifier.height(60.dp))

        Text(
            text = "Create Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6B7280)
        )

        Spacer( modifier = Modifier.height(8.dp))

        //Subtitle
        Text(
            text = "Welcome to EduPay",
            fontSize = 14.sp,
            color = Color(0xFF6B7280)

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

        Column{
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

            if (phoneNumber != null) {
                Text(
                    text = phoneError !!,
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
                disabledContainerColor = Color(0xFF6B7280), // gray when disabled
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
            // Left line
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFF6B7280)
            )
        }

        Spacer( modifier = Modifier.height(24.dp))

        //social login
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            //google button
//            SocialButton()
//        }

    }
}