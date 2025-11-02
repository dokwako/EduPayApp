package com.example.edupayapp.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToChangePin: () -> Unit,
    onLogoutConfirmed: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Show the Logout Confirmation Dialog if showLogoutDialog is true
    if (uiState.showLogoutDialog) {
        LogoutConfirmationDialog(
            onDismiss = { viewModel.onDismissLogoutDialog() },
            onConfirm = onLogoutConfirmed
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    // Profile Loading UI (skeleton)
                    ProfileLoadingState()
                }
                uiState.error != null -> {
                    // Profile Error UI
                    ProfileErrorState(onRetry = { viewModel.fetchProfileData() })
                }
                else -> {
                    // Main Profile Content
                    ProfileContent(
                        uiState = uiState,
                        onNotificationToggle = { viewModel.onNotificationToggle(it) },
                        onNavigateToChangePin = onNavigateToChangePin,
                        onLogoutClicked = { viewModel.onLogoutClicked() }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onNotificationToggle: (Boolean) -> Unit,
    onNavigateToChangePin: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // User Info Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(56.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Denzil Okwako", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "0714322037", color = Color.Gray)
            }
        }

        Divider()

        // Menu Items
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileItemRow(
                icon = Icons.Default.Notifications,
                text = "Notifications",
                trailingContent = {
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = onNotificationToggle
                    )
                }
            )
            Divider()
            ProfileItemRow(
                icon = Icons.Default.Lock,
                text = "Change Pin",
                onClick = onNavigateToChangePin
            )
            Divider()
            ProfileItemRow(
                icon = Icons.Default.Logout,
                text = "Log Out",
                onClick = onLogoutClicked,
                contentColor = Color.Red // Make logout text red
            )
            Divider()
        }
    }
}

@Composable
private fun ProfileItemRow(
    icon: ImageVector,
    text: String,
    contentColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = if (contentColor != Color.Unspecified) contentColor else Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, modifier = Modifier.weight(1f), fontSize = 16.sp, color = contentColor)
        if (trailingContent != null) {
            trailingContent()
        } else if (onClick != null) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Navigate", tint = Color.Gray)
        }
    }
}

// Matches "Profile Loading" screen
@Composable
private fun ProfileLoadingState() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(56.dp).background(Color.LightGray, CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Box(Modifier.height(20.dp).width(120.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(Modifier.height(16.dp).width(80.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            }
        }
        Divider()
        Column(modifier = Modifier.padding(16.dp)) {
            Box(Modifier.fillMaxWidth().height(50.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(50.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
        }
    }
}

// Matches "Profile Error" screen
@Composable
private fun ProfileErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "We couldn't load your profile right now", textAlign = TextAlign.Center, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// Matches "Log Out pop up" screen
@Composable
private fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Are you sure you want to log out?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Log Out")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}