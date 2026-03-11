package com.example.edupayapp.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReceiptDetailScreen(
    studentName: String,
    amount: String,
    itemName: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF9FAFB)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Official Receipt", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text("EduPay Digital Transaction", color = Color.Gray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(24.dp))

                ReceiptDetailRow("Student Name", studentName)
                ReceiptDetailRow("Description", itemName)
                ReceiptDetailRow("Date", "March 09, 2026")
                ReceiptDetailRow("Ref Number", "EP-${(100000..999999).random()}")

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(24.dp))

                Text("Total Paid", color = Color.Gray, fontSize = 14.sp)
                Text("KES $amount", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = Color(0xFF3B82F6))

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { /* Logic for PDF download */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Download Receipt (PDF)", fontWeight = FontWeight.Bold)
                }
            }
        }

        TextButton(onClick = onBack, modifier = Modifier.padding(top = 24.dp)) {
            Text("Back to History", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReceiptDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF6B7280), fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))
    }
}