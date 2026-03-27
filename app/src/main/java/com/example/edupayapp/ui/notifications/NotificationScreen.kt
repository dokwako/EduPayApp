package com.example.edupayapp.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.NotificationItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- The Engine ---
class NotificationViewModel : ViewModel() {
    private val authService = AuthService()
    private val _notifications = MutableStateFlow<List<NotificationItemDto>>(emptyList())
    val notifications = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init { fetchNotifications() }

    private fun fetchNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authService.getNotifications()
                _notifications.value = response.results
            } catch (e: Exception) {
                // Silently handle network errors
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsRead(notification: NotificationItemDto) {
        if (notification.is_read) return // Don't waste network calls if already read

        // Optimistically update the UI instantly
        _notifications.update { currentList ->
            currentList.map {
                if (it.id == notification.id) it.copy(is_read = true) else it
            }
        }

        // Tell the backend in the background
        viewModelScope.launch {
            try {
                authService.markNotificationAsRead(notification.id)
            } catch (e: Exception) {
                // If the network fails, it will just remain unread on the next refresh
            }
        }
    }
}

// --- The UI ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF3B82F6))
                notifications.isEmpty() -> Text("No new notifications.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notifications) { notification ->
                            NotificationCard(notification) {
                                viewModel.markAsRead(notification)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItemDto, onClick: () -> Unit) {
    val backgroundColor = if (notification.is_read) Color.White else Color(0xFFEFF6FF) // Light blue if unread
    val borderColor = if (notification.is_read) Color(0xFFF3F4F6) else Color(0xFFBFDBFE)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFDBEAFE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color(0xFF2563EB))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(notification.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF111827))
                    Text(notification.created_at.take(10), fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(notification.message, fontSize = 13.sp, color = Color(0xFF4B5563), lineHeight = 18.sp)
            }
        }
    }
}