package com.example.edupayapp.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    //  This now properly asks for Name, Amount, and Item to pass to navigation
    onViewReceiptClicked: (String, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> HistoryLoadingSkeleton()
                uiState.error != null -> HistoryErrorState(onRetry = { viewModel.loadDummyHistory() })
                else -> HistoryContent(uiState, viewModel, onViewReceiptClicked)
            }
        }
    }
}

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    viewModel: HistoryViewModel,
    onViewReceiptClicked: (String, String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search", color = Color.Gray) },
            trailingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFF3B82F6)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(uiState.notifications) { notification ->
                NotificationCard(notification) {
                    // Send the data exactly when "View Receipt" is clicked
                    onViewReceiptClicked(notification.studentName, notification.amount, notification.itemName)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem, onReceiptClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.type,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF111827)
                )
                Text(
                    text = notification.date,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = Color(0xFF374151),
                lineHeight = 20.sp
            )

            if (notification.hasReceipt) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "View receipt \u2193",
                        fontSize = 12.sp,
                        color = Color(0xFF3B82F6),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onReceiptClick() }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.WarningAmber, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "We couldn't load notifications right now", textAlign = TextAlign.Center, color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), shape = RoundedCornerShape(8.dp)) {
            Text("Retry", color = Color.White)
        }
    }
}

@Composable
fun HistoryLoadingSkeleton() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Box(Modifier.fillMaxWidth().height(50.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp)))
        Spacer(modifier = Modifier.height(24.dp))
        repeat(4) {
            Box(Modifier.fillMaxWidth().height(100.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp)))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}