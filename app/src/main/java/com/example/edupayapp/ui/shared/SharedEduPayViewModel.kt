package com.example.edupayapp.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.InitiatePaymentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. We define the exact states the UI needs to react to
sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Initiating : PaymentUiState() // Telling the backend to trigger STK
    object WaitingForMpesa : PaymentUiState() // Polling every 10 seconds
    data class Success(val message: String) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class SharedEduPayViewModel : ViewModel() {
    private val authService = AuthService()

    private val _paymentState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentState = _paymentState.asStateFlow()

    // 2. The new Polling Engine
    fun payWithMpesa(obligationId: Int, phoneNumber: String, amount: Int? = null) {

        viewModelScope.launch(Dispatchers.IO) {
            _paymentState.value = PaymentUiState.Initiating

            try {
                // Step A: Trigger the STK Push
                val request = InitiatePaymentRequest(obligationId, phoneNumber, amount?.toString())
                val response = authService.initiatePayment(request)
                val paymentId = response.payment_id

                // Step B: Enter the Waiting Room (Polling Loop)
                _paymentState.value = PaymentUiState.WaitingForMpesa
                var isComplete = false
                var attempts = 0
                val maxAttempts = 12 // 12 attempts * 10 seconds = 2 minutes max wait time

                while (!isComplete && attempts < maxAttempts) {
                    delay(10_000L) // Wait exactly 10 seconds

                    try {
                        // Ask the backend: "Did Safaricom confirm this yet?"
                        val statusResponse = authService.checkPaymentStatus(paymentId)

                        when (statusResponse.status.lowercase()) {
                            "completed", "approved", "successful" -> {
                                _paymentState.value = PaymentUiState.Success("Payment successful!")
                                isComplete = true
                            }
                            "failed", "cancelled" -> {
                                _paymentState.value = PaymentUiState.Error("Payment failed or was cancelled by user.")
                                isComplete = true
                            }
                            // If "pending", the loop just continues to the next 10-second delay
                        }
                    } catch (e: Exception) {
                        // If a single ping fails (e.g., brief network drop), don't crash the whole process.
                        // Just let it try again on the next loop.
                    }
                    attempts++
                }

                if (!isComplete) {
                    _paymentState.value = PaymentUiState.Error("Payment timed out. Please check your M-Pesa messages.")
                }

            } catch (e: Exception) {

                println("🚨 PAYMENT CRASH REASON: ${e.message}")
                e.printStackTrace()
                _paymentState.value = PaymentUiState.Error(e.message ?: "Failed to initiate payment.")
            }
        }
    }

    // Reset the state so the user can try again if they want
    fun resetPaymentState() {
        _paymentState.value = PaymentUiState.Idle
    }
}