package com.example.edupayapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationItem(
    val id: String,
    val type: String, // "Payment Reminder" or "Payment Received"
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
                    id = "1",
                    type = "Payment Reminder",
                    message = "Trip payment for Mary Kerubo is due in 3 days",
                    date = "Tue 10:30AM",
                    hasReceipt = true // Figma shows "View receipt" on reminders too
                ),
                NotificationItem(
                    id = "2",
                    type = "Payment Received",
                    message = "Your payment of KES.6,700 for Andrew Ochieng's uniform has been recieved.",
                    date = "Mon 17:23PM",
                    hasReceipt = true
                ),
                NotificationItem(
                    id = "3",
                    type = "Payment Reminder",
                    message = "Trip payment for Mary Kerubo is due in 3 days",
                    date = "Sun 10:30AM",
                    hasReceipt = true
                ),
                NotificationItem(
                    id = "4",
                    type = "Payment Reminder",
                    message = "Ann Mwende's school fees balance of KES. 1700 is due nex week on Wednesday 25/08/2025.",
                    date = "18/08/2025",
                    hasReceipt = true
                )
            )

            _uiState.update { it.copy(isLoading = false, notifications = dummyData) }

            // To test the error screen , uncomment
            // _uiState.update { it.copy(isLoading = false, error = "Failed to load") }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}