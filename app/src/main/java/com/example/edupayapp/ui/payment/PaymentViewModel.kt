package com.example.edupayapp.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.InitiatePaymentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. ADDED WAITING STATE
enum class PaymentState { INPUT, LOADING, WAITING_FOR_MPESA, SUCCESS, ERROR }

data class PaymentUiState(
    val paymentState: PaymentState = PaymentState.INPUT,
    val studentId: String = "",
    val studentName: String = "",
    val school: String = "",
    val status: String = "",
    val item: String = "",
    val fetchedTotal: String = "",
    val customAmount: String = "",
    val phoneNumber: String = "",
    val paymentId: String? = null
)

class PaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()
    private val authService = AuthService()

    fun initPaymentData(id: String, name: String, school: String, total: String, status: String, item: String) {
        _uiState.update { it.copy(
            studentId = id,
            studentName = name,
            school = school,
            fetchedTotal = total,
            status = status,
            item = item,
            customAmount = total
        ) }
    }

    fun updatePhoneNumber(num: String) { _uiState.update { it.copy(phoneNumber = num) } }
    fun updateCustomAmount(amt: String) { _uiState.update { it.copy(customAmount = amt) } }

    fun initiatePayment() {
        viewModelScope.launch {
            _uiState.update { it.copy(paymentState = PaymentState.LOADING) }

            try {
                var mpesaNum = _uiState.value.phoneNumber.trim()
                if (mpesaNum.startsWith("0")) mpesaNum = "254" + mpesaNum.drop(1)
                else if (mpesaNum.startsWith("+")) mpesaNum = mpesaNum.drop(1)

                val amountToPay = _uiState.value.customAmount
                    .replace(",", "")
                    .replace("KES", "")
                    .trim()


                // 2. TRIGGER STK PUSH
                val response = authService.initiatePayment(
                    InitiatePaymentRequest(
                        obligation_id = _uiState.value.studentId.toIntOrNull() ?: 0,
                        phone_number = mpesaNum,
                        amount = amountToPay
                    )
                )

                val pId = response.payment_id

                // 3. ENTER THE WAITING ROOM
                _uiState.update { it.copy(paymentState = PaymentState.WAITING_FOR_MPESA, paymentId = pId) }

                var isComplete = false
                var attempts = 0
                val maxAttempts = 12 // 12 checks * 10 seconds = 2 minutes max wait

                while (!isComplete && attempts < maxAttempts) {
                    delay(10_000L) // Wait exactly 10 seconds

                    try {
                        // Ask backend for Safaricom's update
                        val statusResponse = authService.checkPaymentStatus(pId)

                        when (statusResponse.status.lowercase()) {
                            "completed", "approved", "successful" -> {
                                _uiState.update { it.copy(paymentState = PaymentState.SUCCESS) }
                                isComplete = true
                            }
                            "failed", "cancelled" -> {
                                _uiState.update { it.copy(paymentState = PaymentState.ERROR) }
                                isComplete = true
                            }
                            // If it is still "pending", the loop just continues
                        }
                    } catch (e: Exception) {
                        // Ignore a single network drop and try again in 10 seconds
                    }
                    attempts++
                }

                // If 2 minutes pass and we are still looping, it timed out
                if (!isComplete) {
                    _uiState.update { it.copy(paymentState = PaymentState.ERROR) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(paymentState = PaymentState.ERROR) }
            }
        }
    }

    fun resetPayment() {
        _uiState.update { it.copy(paymentState = PaymentState.INPUT, phoneNumber = "", paymentId = null) }
    }
}