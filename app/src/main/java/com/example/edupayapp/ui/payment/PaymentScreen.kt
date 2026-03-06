package com.example.edupayapp.ui.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    studentId: String,   // NEW: This is our Obligation ID
    studentName: String,
    schoolName: String,
    amount: String,
    status: String,      // NEW: Dynamic status (Overdue, Due soon, etc.)
    viewModel: PaymentViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onViewReceipt: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Pass ALL dynamic data including ID and Status to the ViewModel
    LaunchedEffect(studentId, studentName, schoolName, amount, status) {
        viewModel.initPaymentData(studentId, studentName, schoolName, amount, status)
    }

    Scaffold(
        topBar = {
            if (uiState.paymentState == PaymentState.INPUT) {
                CenterAlignedTopAppBar(
                    title = { Text("Payments", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (uiState.paymentState) {
                PaymentState.INPUT -> PaymentInputContent(uiState, viewModel)
                PaymentState.LOADING -> PaymentLoadingContent()
                PaymentState.SUCCESS -> PaymentSuccessContent(onViewReceipt)
                PaymentState.ERROR -> PaymentErrorContent(onRetry = { viewModel.resetPayment() })
            }
        }
    }
}

@Composable
fun PaymentInputContent(uiState: PaymentUiState, viewModel: PaymentViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        // 1. Payment Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Payment Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
                Text("Details on Payments", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                // Now showing the dynamic values from the UI State
                SummaryRow("Student Name:", uiState.studentName)
                SummaryRow("School:", uiState.school)

                // Color coding the status text based on the value
                val statusColor = when(uiState.status) {
                    "Overdue" -> Color(0xFFEF4444) // Red
                    "Due soon" -> Color(0xFFF59E0B) // Amber
                    else -> Color(0xFF10B981) // Green
                }
                SummaryRow("Status:", uiState.status, valueColor = statusColor)

                SummaryRow("Item:", uiState.item)
                SummaryRow("Total Amount:", uiState.amount, valueColor = Color(0xFF3B82F6), isBold = true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. M-Pesa Details Section
        Text("M-Pesa details", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF111827))
        Text("Enter your M-pesa Details", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Enter your M-Pesa number", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            placeholder = { Text("e.g. 0712345678") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Click Pay now to receive a prompt for the payment",
            fontSize = 10.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.initiatePayment() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            enabled = uiState.phoneNumber.isNotBlank()
        ) {
            Text("Pay Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = Color(0xFF374151), isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun PaymentLoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF3B82F6))
    }
}

@Composable
fun PaymentSuccessContent(onViewReceipt: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = "Success", tint = Color(0xFF3B82F6), modifier = Modifier.size(60.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Payment Made successfully!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onViewReceipt,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
        ) {
            Text("View Receipt", fontSize = 16.sp)
        }
    }
}

@Composable
fun PaymentErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.WarningAmber, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(60.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "We couldn't Process your payment right now. Please try again later",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Retry", color = Color.White)
        }
    }
}