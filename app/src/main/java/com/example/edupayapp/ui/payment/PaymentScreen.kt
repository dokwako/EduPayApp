package com.example.edupayapp.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.edupayapp.ui.shared.SharedEduPayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    sharedViewModel: SharedEduPayViewModel,
    obligationId: String, // CHANGED THIS FROM studentId
    studentName: String,
    schoolName: String,
    amount: String,
    status: String,
    itemName: String,
    viewModel: PaymentViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onViewReceipt: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(obligationId) { // CHANGED THIS FROM studentId
        viewModel.initPaymentData(obligationId, studentName, schoolName, amount, status, itemName) // CHANGED THIS FROM studentId
    }

    Scaffold(
        topBar = {
            if (uiState.paymentState == PaymentState.INPUT) {
                TopAppBar(
                    title = { Text("Payments", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (uiState.paymentState) {
                PaymentState.INPUT -> PaymentInputContent(uiState, viewModel)

                // Show different messages depending on the loop stage
                PaymentState.LOADING -> PaymentLoadingContent("Connecting to Safaricom...")
                PaymentState.WAITING_FOR_MPESA -> PaymentLoadingContent("Please enter your PIN on your phone...\nWaiting for confirmation.")

                PaymentState.SUCCESS -> PaymentSuccessContent(uiState.paymentId ?: "", onViewReceipt)
                PaymentState.ERROR -> PaymentErrorContent { viewModel.resetPayment() }
            }
        }
    }
}

@Composable
fun PaymentInputContent(uiState: PaymentUiState, viewModel: PaymentViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Details on Payments", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                FigmaRow("Student Name:", uiState.studentName)
                FigmaRow("School:", uiState.school)

                val statusColor = if(uiState.status == "Overdue") Color.Red else Color(0xFFF59E0B)
                FigmaRow("Status:", uiState.status, valueColor = statusColor)

                FigmaRow("Item:", uiState.item)
                FigmaRow("Total Amount:", "KES ${uiState.fetchedTotal}", valueColor = Color(0xFF3B82F6), isBold = true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("M-Pesa details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Enter your M-pesa Details", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter Amount to Pay", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = uiState.customAmount,
            onValueChange = { viewModel.updateCustomAmount(it) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter your M-Pesa number", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("07XXXXXXXX") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.initiatePayment() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            enabled = uiState.phoneNumber.length >= 10 && uiState.customAmount.isNotEmpty()
        ) {
            Text("Pay Now", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FigmaRow(label: String, value: String, valueColor: Color = Color.Black, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium)
    }
}

// UPDATED TO ACCEPT MESSAGES
@Composable
fun PaymentLoadingContent(message: String) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator(color = Color(0xFF3B82F6))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
    }
}

@Composable
fun PaymentSuccessContent(id: String, onReceipt: (String) -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Filled.Check, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(80.dp))
        Text("Payment Made successfully!", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onReceipt(id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))) { Text("View Receipt") }
    }
}

@Composable
fun PaymentErrorContent(onRetry: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Outlined.WarningAmber, null, tint = Color.Red, modifier = Modifier.size(60.dp))
        Text("Payment Failed or Cancelled", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}