package com.example.edupayapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    // FIX: Transparent colors force it to blend perfectly with the Scaffold background
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFF9FAFB) // Light gray background from Figma
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                uiState.isLoading -> {
                    ProfileLoadingState()
                }
                uiState.error != null -> {
                    ProfileErrorState(onRetry = { viewModel.fetchProfileData() })
                }
                else -> {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. User Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    // Dynamically get initials from userName
                    val initials = uiState.userName.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")
                        .uppercase()

                    Text(initials.ifEmpty { "P" }, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name and Phone
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.userName.ifEmpty { "Parent Name" }, // Uses dynamic name
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = "+254-714 322 037", // Leave hardcoded until backend adds phone numbers
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Edit Profile",
                    tint = Color(0xFF9CA3AF)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Settings Menu Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                // Notifications Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFF6B7280))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Notifications", fontSize = 16.sp, color = Color(0xFF374151))
                    }
                    Switch(
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = onNotificationToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF3B82F6)
                        )
                    )
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                // Change PIN Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToChangePin() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFF6B7280))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Change Pin", fontSize = 16.sp, color = Color(0xFF374151))
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFF9CA3AF))
                }

                HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

                // Log Out Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogoutClicked() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Log Out", fontSize = 16.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// Matches "Profile Error" screen from Figma
@Composable
private fun ProfileErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.WarningAmber, contentDescription = "Error", tint = Color.Red, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "We couldn't load your profile right now", textAlign = TextAlign.Center, color = Color(0xFF6B7280))
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Retry", color = Color.White)
        }
    }
}

// Matches "Log Out pop up" screen from Figma
@Composable
private fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                "Are you sure you want to log out?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Log Out", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = Color(0xFF374151))
            }
        }
    )
}

// Minimal loading skeleton to match Figma structure
@Composable
private fun ProfileLoadingState() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp)))
        Spacer(modifier = Modifier.height(32.dp))
        Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp)))
    }
}