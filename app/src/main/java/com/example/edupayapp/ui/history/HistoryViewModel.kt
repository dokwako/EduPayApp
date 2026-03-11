package com.example.edupayapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// FIX: Added studentName, itemName, and amount so the receipt has data to show
data class NotificationItem(
    val id: String,
    val type: String,
    val studentName: String,
    val itemName: String,
    val amount: String,
    val message: String,
    val date: String,
    val hasReceipt: Boolean = false
)

data class HistoryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val notifications: List<NotificationItem> = emptyList(),
    val searchQuery: String = ""
)

class HistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDummyHistory()
    }

    fun loadDummyHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            delay(1500) // Simulate network loading for the skeleton UI

            val dummyData = listOf(
                NotificationItem(
                    id = "0",
                    type = "Payment Received",
                    studentName = "Faith Kimani",
                    itemName = "School Fees",
                    amount = "1",
                    message = "Your payment for Faith Kimani's School Fees has been received.",
                    date = "Just Now",
                    hasReceipt = true
                ),
                NotificationItem(
                    id = "2",
                    type = "Payment Received",
                    studentName = "Andrew Ochieng",
                    itemName = "Uniform",
                    amount = "6700",
                    message = "Your payment of KES.6,700 for Andrew Ochieng's uniform has been received.",
                    date = "Mon 17:23PM",
                    hasReceipt = true
                ),
                NotificationItem(
                    id = "1",
                    type = "Payment Reminder",
                    studentName = "Mary Kerubo",
                    itemName = "Trip",
                    amount = "2500",
                    message = "Trip payment for Mary Kerubo is due in 3 days",
                    date = "Tue 10:30AM",
                    hasReceipt = true
                ),
                NotificationItem(
                    id = "4",
                    type = "Payment Reminder",
                    studentName = "Ann Mwende",
                    itemName = "School Fees",
                    amount = "1700",
                    message = "Ann Mwende's school fees balance of KES. 1700 is due next week.",
                    date = "18/08/2025",
                    hasReceipt = true
                )
            )

            _uiState.update { it.copy(isLoading = false, notifications = dummyData) }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}