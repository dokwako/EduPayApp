package com.example.edupayapp.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.ChevronRight
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
import java.text.NumberFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onLoggedOut: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToReceipts: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onChildClicked: (String, String, String, String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopBar()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar() {
    TopAppBar(
        title = {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF10B981), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)) {
                        append("Edu")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF3B82F6), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)) {
                        append("Pay")
                    }
                }
            )
        },
        actions = {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) { Text("5") }
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF374151)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF9FAFB),
            scrolledContainerColor = Color(0xFFF9FAFB)
        )
    )
}

@Composable
fun DashboardBottomNavigation(
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Stay on Dashboard */ },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF3B82F6),
                selectedTextColor = Color(0xFF3B82F6),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHistory,
            icon = { Icon(Icons.Outlined.Schedule, contentDescription = "History") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Outlined.PersonOutline, contentDescription = "Profile") }
        )
    }
}

@Composable
fun DashboardContent(
    summary: DashboardSummaryResponse,
    onChildClicked: (String, String, String, String, String) -> Unit,
    onNavigateToReceipts: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            val userName = summary.user.full_name.substringBefore(" ")
            BlueBalanceCard(
                userName = userName,
                financialSummary = summary.financial_summary
            )
        }

        item {
            Text(
                text = "My Children",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(summary.children) { child ->
            ChildSummaryCard(
                child = child,
                onChildClicked = {
                    val fullName = "${child.first_name} ${child.last_name}"
                    val formattedAmount = formatCurrency(child.outstanding_balance, "KES")

                    val statusText = when {
                        child.outstanding_balance > 20000 -> "Overdue"
                        child.outstanding_balance > 0 -> "Due soon"
                        else -> "Paid"
                    }

                    onChildClicked(
                        child.student_id.toString(),
                        fullName,
                        child.school_name,
                        formattedAmount,
                        statusText
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuickActionsRow(
                onNavigateToReceipts = onNavigateToReceipts,
                onNavigateToHelp = onNavigateToHelp
            )
        }
    }
}

@Composable
fun BlueBalanceCard(userName: String, financialSummary: FinancialSummary) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hello $userName!",
                    color = Color(0xFFD1FAE5),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { isBalanceVisible = !isBalanceVisible },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Balance",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Total Outstanding Balance",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBalanceVisible) {
                    formatCurrency(financialSummary.total_outstanding_balance, financialSummary.currency)
                } else {
                    "KES ***,***"
                },
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun ChildSummaryCard(child: ChildDetail, onChildClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChildClicked() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF84CC16), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = child.first_name.first().toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${child.first_name} ${child.last_name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = child.school_name,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = child.student_class,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(child.outstanding_balance, "KES"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(4.dp))

                val (pillColor, pillText) = when {
                    child.outstanding_balance > 20000 -> Color(0xFFEF4444) to "Overdue"
                    child.outstanding_balance > 0 -> Color(0xFFF59E0B) to "Due soon"
                    else -> Color(0xFF9CA3AF) to "Paid"
                }

                Box(
                    modifier = Modifier
                        .background(pillColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = pillText, color = pillColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "View details",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    onNavigateToReceipts: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuickActionCard(
            title = "Receipts",
            icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = Color.Gray) },
            onClick = onNavigateToReceipts
        )
        QuickActionCard(
            title = "Help",
            icon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = Color.Gray) },
            onClick = onNavigateToHelp
        )
    }
}

@Composable
fun QuickActionCard(title: String, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(90.dp)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontSize = 12.sp, color = Color(0xFF374151))
    }
}

fun formatCurrency(amount: Double, currency: String): String {
    val numericAmount = if (amount.isNaN()) 0.0 else amount
    val format = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
    format.currency = Currency.getInstance(currency)
    return format.format(numericAmount).replace(".00", "")
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
            text = message,
            color = Color.Red,
            fontSize = 16.sp
        )
    }
}