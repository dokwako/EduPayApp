package com.example.edupayapp.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.data.ChildDetail
import com.example.edupayapp.data.DashboardSummaryResponse
import com.example.edupayapp.data.FinancialSummary
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onLoggedOut: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReceipts: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onChildClicked: (String, String, String, String, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                // This connects the UI to the ViewModel's state
                unreadCount = uiState.unreadNotificationCount,
                onNavigateToNotifications = onNavigateToNotifications
            )
        },
        bottomBar = {
            DashboardBottomNavigation(
                onNavigateToHistory = onNavigateToHistory,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorMessage(uiState.error!!)
                uiState.summary != null -> DashboardContent(
                    summary = uiState.summary!!,
                    onChildClicked = onChildClicked,
                    onNavigateToReceipts = onNavigateToReceipts,
                    onNavigateToHelp = onNavigateToHelp
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    summary: DashboardSummaryResponse,
    onChildClicked: (String, String, String, String, String, String) -> Unit,
    onNavigateToReceipts: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            BlueBalanceCard(
                userName = summary.user.full_name.substringBefore(" "),
                financialSummary = summary.financial_summary
            )
        }

        item {
            Text("My Children", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
        }

        items(summary.children) { child ->
            ChildSummaryCard(
                child = child,
                onChildClicked = {
                    val fullName = "${child.first_name} ${child.last_name}"

                    // --- NEW CODE: Find the real obligation for this child ---
                    // We look through the upcoming_obligations list to find the one matching this child
                    val realObligation = summary.upcoming_obligations.firstOrNull {
                        it.student_name.contains(child.first_name, ignoreCase = true)
                    }

                    // Grab the real obligation ID (like 1206), or default to "0" if missing
                    val obligationIdToPay = realObligation?.id?.toString() ?: "0"

                    // Grab the real item name (like "Lab Fee"), or use your fallback
                    val actualItemName = realObligation?.description ?: if (child.outstanding_balance < 5000 && child.outstanding_balance > 0) "Trip" else "School Fees"

                    val rawAmount = child.outstanding_balance.toInt().toString()

                    val statusText = when {
                        child.outstanding_balance > 20000 -> "Overdue"
                        child.outstanding_balance > 0 -> "Due soon"
                        else -> "Paid"
                    }

                    onChildClicked(
                        android.net.Uri.encode(obligationIdToPay), // NOW PASSES THE OBLIGATION ID!
                        android.net.Uri.encode(fullName),
                        android.net.Uri.encode(child.school_name),
                        android.net.Uri.encode(rawAmount),
                        android.net.Uri.encode(statusText),
                        android.net.Uri.encode(actualItemName) // NOW PASSES THE REAL FEE NAME!
                    )
                }
            )
        }

        item {
            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
            Spacer(modifier = Modifier.height(8.dp))
            QuickActionsRow(onNavigateToReceipts = onNavigateToReceipts, onNavigateToHelp = onNavigateToHelp)
        }
    }
}

@Composable
fun BlueBalanceCard(userName: String, financialSummary: FinancialSummary) {
    var isBalanceVisible by remember { mutableStateOf(true) }
    val gradientBrush = Brush.linearGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)))

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(gradientBrush).padding(24.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Hello $userName!", color = Color(0xFFD1FAE5), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                IconButton(onClick = { isBalanceVisible = !isBalanceVisible }) {
                    Icon(if (isBalanceVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null, tint = Color.White)
                }
            }
            Text("Total Outstanding Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text(
                text = if (isBalanceVisible) "Ksh ${String.format("%,.0f", financialSummary.total_outstanding_balance)}" else "Ksh ••••••",
                color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun ChildSummaryCard(child: ChildDetail, onChildClicked: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onChildClicked() },
        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFF84CC16), CircleShape), contentAlignment = Alignment.Center) {
                Text(child.first_name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${child.first_name} ${child.last_name}", fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text(child.school_name, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Ksh ${child.outstanding_balance.toInt()}", fontWeight = FontWeight.Bold)
                val (pillColor, pillText) = when {
                    child.outstanding_balance > 20000 -> Color(0xFFEF4444) to "Overdue"
                    child.outstanding_balance > 0 -> Color(0xFFF59E0B) to "Due soon"
                    else -> Color(0xFF10B981) to "Paid"
                }
                Box(modifier = Modifier.background(pillColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text(pillText, color = pillColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    unreadCount: Int, // The dynamic count injected from the Scaffold
    onNavigateToNotifications: () -> Unit
) {
    TopAppBar(
        title = {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(Color(0xFF10B981), fontWeight = FontWeight.ExtraBold)) { append("Edu") }
                withStyle(SpanStyle(Color(0xFF3B82F6), fontWeight = FontWeight.ExtraBold)) { append("Pay") }
            })
        },
        actions = {
            IconButton(onClick = onNavigateToNotifications) {
                BadgedBox(
                    badge = {
                        // Only draw the red dot if there are actually unread notifications
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White,
                                modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                            ) {
                                Text("$unreadCount", fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF111827)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF9FAFB))
    )
}

@Composable
fun DashboardBottomNavigation(onNavigateToHistory: () -> Unit, onNavigateToProfile: () -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.Home, null) }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = onNavigateToHistory, icon = { Icon(Icons.Outlined.Schedule, null) }, label = { Text("History") })
        NavigationBarItem(selected = false, onClick = onNavigateToProfile, icon = { Icon(Icons.Outlined.PersonOutline, null) }, label = { Text("Profile") })
    }
}

@Composable
fun QuickActionsRow(onNavigateToReceipts: () -> Unit, onNavigateToHelp: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        QuickActionCard("Receipts", Icons.Outlined.ReceiptLong, onNavigateToReceipts)
        QuickActionCard("Help", Icons.Outlined.HelpOutline, onNavigateToHelp)
    }
}

@Composable
fun QuickActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = Color.Gray)
            Text(title, fontSize = 12.sp)
        }
    }
}

@Composable
fun LoadingIndicator() { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF3B82F6)) } }

@Composable
fun ErrorMessage(msg: String) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(msg, color = Color.Red) } }