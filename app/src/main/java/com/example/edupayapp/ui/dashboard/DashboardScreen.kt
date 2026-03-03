package com.example.edupayapp.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// --- FIX: ADD DATA MODEL IMPORTS ---
import com.example.edupayapp.data.ChildDetail
import com.example.edupayapp.data.DashboardSummaryResponse
import com.example.edupayapp.data.FinancialSummary
import com.example.edupayapp.data.RecentTransaction
// ------------------------------------
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onLoggedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(onLogoutClicked = {
                viewModel.logout()
                onLoggedOut()
            })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF7F7F7))) {
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorMessage(uiState.error!!)
                uiState.summary != null -> DashboardContent(uiState.summary!!)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onLogoutClicked: () -> Unit) {
    TopAppBar(
        title = { Text("EduPay Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3B82F6)),
        actions = {
            IconButton(onClick = onLogoutClicked) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
            }
        }
    )
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF3B82F6))
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Error: $message",
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DashboardContent(summary: DashboardSummaryResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome and Financial Summary Card
        item {
            val userName = summary.user.full_name.substringBefore(" ")
            Text(
                text = "Hello, $userName!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF27272A)
            )
            Spacer(modifier = Modifier.height(16.dp))
            FinancialSummaryCard(summary.financial_summary)
        }

        // 2. Outstanding Obligations Title
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your Children's Status",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF27272A)
            )
        }

        // 3. Children List (Outstanding Balances)
        items(summary.children) { child ->
            ChildSummaryCard(child)
        }

        // 4. Recent Transactions Title
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recent Transactions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF27272A)
            )
        }

        // 5. Recent Transactions List
        items(summary.recent_transactions.take(3)) { transaction ->
            RecentTransactionItem(transaction)
        }
    }
}

// Utility function for formatting currency (assuming KES)
fun formatCurrency(amount: Double, currency: String): String {
    val numericAmount = if (amount.isNaN()) 0.0 else amount
    val format = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    format.currency = Currency.getInstance(currency)
    return format.format(numericAmount)
}

@Composable
fun FinancialSummaryCard(summary: FinancialSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Total Outstanding Balance", fontSize = 16.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(summary.total_outstanding_balance, summary.currency),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFDC2626)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Total Paid", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = formatCurrency(summary.total_paid, summary.currency),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }

                Button(onClick = { /* Navigate to Pay Screen */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))) {
                    Text("Pay Now")
                }
            }
        }
    }
}

@Composable
fun ChildSummaryCard(child: ChildDetail) {
    val balanceText = if (child.outstanding_balance > 0) {
        "Balance: ${formatCurrency(child.outstanding_balance, "KES")}"
    } else {
        "Balance: Paid Up!"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(child.first_name.first().toString(), color = Color(0xFF3B82F6), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${child.first_name} ${child.last_name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${child.student_class} | ${child.school_name}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = balanceText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (child.outstanding_balance > 0) Color(0xFFDC2626) else Color(0xFF10B981)
            )
        }
    }
}

@Composable
fun RecentTransactionItem(transaction: RecentTransaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${transaction.method} Payment",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Txn ID: ${transaction.txn_id}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(transaction.amount.toDoubleOrNull() ?: 0.0, "KES"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = transaction.created_at.substringBefore("T"),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}