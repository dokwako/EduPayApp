package com.example.edupayapp.ui.dashboard

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Updated Child data class to match the UI's needs
data class Child(
    val name: String,
    val school: String,
    val grade: String,
    val balance: Double,
    val status: String,
    val statusColor: Color,
    val studentId: String
)

// Updated UI State to match the UI's needs
data class DashboardUiState(
    val userName: String = "John Doe",
    val totalBalance: Double = 0.0,
    val children: List<Child> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val notificationCount: Int = 3
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500) // Simulate network delay

            // Updated dummy data to match the Figma design
            val dummyChildren = listOf(
                Child(
                    name = "Liam Lawson",
                    school = "Nakuru High School",
                    grade = "10",
                    balance = 2500.0,
                    status = "Due soon",
                    statusColor = Color(0xFFFFA500), // Orange
                    studentId = "1"
                ),
                Child(
                    name = "Liam Lawson",
                    school = "Nakuru High School",
                    grade = "10",
                    balance = 2500.0,
                    status = "Up to date",
                    statusColor = Color(0xFF16A34A), // Green
                    studentId = "2"
                ),
                Child(
                    name = "Liam Lawson",
                    school = "Nakuru High School",
                    grade = "10",
                    balance = 2500.0,
                    status = "Overdue",
                    statusColor = Color.Red,
                    studentId = "3"
                )
            )
            val totalBalance = dummyChildren.sumOf { it.balance }

            // Update state with new dummy data
            _uiState.update {
                it.copy(
                    isLoading = false,
                    children = dummyChildren,
                    totalBalance = totalBalance,
                    userName = "John Doe!",
                    notificationCount = 3
                )
            }
        }
    }
}