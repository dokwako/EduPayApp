package com.example.edupayapp.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import all filled icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Import ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.R // Make sure R is imported

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(),
    onNavigateToReceipts: () -> Unit, // Changed from History
    onNavigateToProfile: () -> Unit,
    onNavigateToChildDetails: (studentId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                notificationCount = uiState.notificationCount,
                onNotificationClick = { /* TODO */ }
            )
        },
        bottomBar = {
            DashboardBottomNavigationBar(
                onNavigateToReceipts = onNavigateToReceipts,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    DashboardErrorState()
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { TotalBalanceCard(userName = uiState.userName, balance = uiState.totalBalance) }
                        item {
                            Text(
                                text = "My Children",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(uiState.children) { child ->
                            ChildCard(
                                child = child,
                                onClick = { onNavigateToChildDetails(child.studentId) }
                            )
                        }
                        item { QuickActionsSection(onNavigateToReceipts, onNavigateToHelp) }
                    }
                }
            }
        }
    }
}

// --- Composable Sections ---

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopAppBar(
    notificationCount: Int,
    onNotificationClick: () -> Unit
) {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "EduPay Logo",
                modifier = Modifier.height(28.dp)
            )
        },
        actions = {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge { Text("$notificationCount") }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }
        }
    )
}

@Composable
private fun TotalBalanceCard(userName: String, balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3B82F6))
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hello, $userName!", color = Color.White, fontSize = 18.sp)
                Icon(
                    Icons.Default.Visibility, // Eye icon
                    contentDescription = "Toggle visibility",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Total Outstanding Balance", color = Color.White.copy(alpha = 0.8f))
            Text(
                "KES ${"%,.2f".format(balance)}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChildCard(child: Child, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick // Make the whole card clickable
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for initial/avatar if needed
            // Box(modifier = Modifier.size(40.dp).background(Color.Gray, CircleShape))
            // Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(child.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text("${child.school} - Grade ${child.grade}", color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "KES ${"%,.2f".format(child.balance)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    child.status,
                    color = child.statusColor, // Use dynamic color from data
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Default.ChevronRight, // Arrow icon replaces Pay Now button
                contentDescription = "View Details",
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onReceiptsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionItem(icon = Icons.Default.Receipt, label = "Receipts", onClick = onReceiptsClick)
            QuickActionItem(icon = Icons.Default.HelpOutline, label = "Help", onClick = onHelpClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.QuickActionItem(
    icon: ImageVector, // Use ImageVector for Material Icons
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DashboardBottomNavigationBar(
    onNavigateToReceipts: () -> Unit, // Updated parameter
    onNavigateToHelp: () -> Unit // Updated parameter
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { /* Already on Home */ }
        )
        // Changed to Receipts
        NavigationBarItem(
            icon = { Icon(Icons.Default.Receipt, contentDescription = "Receipts") },
            label = { Text("Receipts") },
            selected = false,
            onClick = onNavigateToReceipts
        )
        // Changed to Help
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = onNavigateToProfile
        )
    }
}

@Composable
private fun DashboardErrorState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Warning, // Or use a specific error icon
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Couldn't fetch Dashboard data right now.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: Implement retry logic in ViewModel */ }) {
            Text("Retry")
        }
    }
}