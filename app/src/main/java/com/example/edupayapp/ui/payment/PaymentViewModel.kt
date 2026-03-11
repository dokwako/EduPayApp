package com.example.edupayapp.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edupayapp.data.AuthService
import com.example.edupayapp.data.InitiatePaymentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PaymentState { INPUT, LOADING, SUCCESS, ERROR }

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

    // This function catches everything sent from the Dashboard automatically
    fun initPaymentData(id: String, name: String, school: String, total: String, status: String, item: String) {
        _uiState.update { it.copy(
            studentId = id,
            studentName = name,
            school = school,
            fetchedTotal = total,
            status = status,
            item = item,
            customAmount = total // Initially fills the box with the total, but you can change it
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

                // Uses the customAmount you typed (e.g., 1 Shilling) for the API call
                val amountToPay = _uiState.value.customAmount
                    .replace(",", "")
                    .replace("KES", "")
                    .trim()
                    .toIntOrNull() ?: 0

                val response = authService.initiatePayment(
                    InitiatePaymentRequest(
                        obligation_id = _uiState.value.studentId.toIntOrNull() ?: 0,
                        phone_number = mpesaNum,
                        amount = amountToPay
                    )
                )
                _uiState.update { it.copy(paymentState = PaymentState.SUCCESS, paymentId = response.payment_id) }
            } catch (e: Exception) {
                _uiState.update { it.copy(paymentState = PaymentState.ERROR) }
            }
        }
    }

    fun resetPayment() {
        _uiState.update { it.copy(paymentState = PaymentState.INPUT, phoneNumber = "", paymentId = null) }
    }
}