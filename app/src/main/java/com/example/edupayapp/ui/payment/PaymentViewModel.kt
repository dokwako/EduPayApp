package com.example.edupayapp.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.InitiatePaymentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PaymentState {
    INPUT, LOADING, SUCCESS, ERROR
}

data class PaymentUiState(
    val paymentState: PaymentState = PaymentState.INPUT,
    val studentId: String = "",
    val studentName: String = "",
    val school: String = "",
    val status: String = "Pending",
    val item: String = "School Fees",
    val amount: String = "",
    val phoneNumber: String = ""
)

class PaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    // AuthService handles its own HttpClient and Tokens internally
    private val authService = AuthService()

    fun initPaymentData(id: String, name: String, schoolName: String, amountDue: String, paymentStatus: String) {
        _uiState.update {
            it.copy(
                studentId = id,
                studentName = name,
                school = schoolName,
                amount = amountDue,
                status = paymentStatus
            )
        }
    }

    fun updatePhoneNumber(number: String) {
        _uiState.update { it.copy(phoneNumber = number) }
    }

    fun initiatePayment() {
        viewModelScope.launch {
            _uiState.update { it.copy(paymentState = PaymentState.LOADING) }

            try {
                // Format: Ensure it starts with 254 for Safaricom
                var mpesaNumber = _uiState.value.phoneNumber.trim()
                if (mpesaNumber.startsWith("0")) {
                    mpesaNumber = "254" + mpesaNumber.drop(1)
                } else if (mpesaNumber.startsWith("+")) {
                    mpesaNumber = mpesaNumber.drop(1)
                }

                val requestBody = InitiatePaymentRequest(
                    // Using studentId from navigation as the obligation_id
                    obligation_id = _uiState.value.studentId.toIntOrNull() ?: 0,
                    phone_number = mpesaNumber,
                    amount = null
                )

                // The Ktor Auth plugin in AuthService handles the Bearer token automatically
                authService.initiatePayment(requestBody)

                _uiState.update { it.copy(paymentState = PaymentState.SUCCESS) }

            } catch (e: Exception) {
                _uiState.update { it.copy(paymentState = PaymentState.ERROR) }
            }
        }
    }

    fun resetPayment() {
        _uiState.update { it.copy(paymentState = PaymentState.INPUT, phoneNumber = "") }
    }
}