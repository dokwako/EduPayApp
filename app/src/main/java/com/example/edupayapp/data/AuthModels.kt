package com.example.edupayapp.data

import kotlinx.serialization.Serializable

// --- Storage Interface ---
interface TokenStorage {
    fun saveToken(token: String, email: String, userId: String)
    fun getToken(): String?
    fun getEmail(): String?
    fun getUserId(): String?
    fun clear()
}

// In-Memory implementation for testing
class InMemoryTokenStorage : TokenStorage {
    private var accessToken: String? = null
    private var userEmail: String? = null
    private var userId: String? = null

    override fun saveToken(token: String, email: String, userId: String) {
        accessToken = token
        userEmail = email
        this.userId = userId
    }

    override fun getToken(): String? = accessToken
    override fun getEmail(): String? = userEmail
    override fun getUserId(): String? = userId

    override fun clear() {
        accessToken = null
        userEmail = null
        userId = null
    }
}

// --- Requests ---
@Serializable
data class ParentRegisterRequest(
    val email: String,
    val full_name: String,
    val id_number: String
)

@Serializable
data class OtpVerificationRequest(
    val email: String,
    val otp: String
)

@Serializable
data class PinSetupRequest(
    val email: String,
    val pin: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password_or_pin: String
)

@Serializable
data class InitiatePaymentRequest(
    val obligation_id: Int,
    val phone_number: String,
    val amount: String? = null
)

// --- Responses ---
@Serializable
data class InitiatePaymentResponse(
    val payment_id: String,
    val message: String? = null
)

@Serializable
data class AuthMessageResponse(
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val user: UserData
)

@Serializable
data class UserData(
    val id: String,
    val email: String,
    val user_metadata: UserMetadata
)

@Serializable
data class UserMetadata(
    val user_type: String,
    val full_name: String
)

// --- Dashboard Models ---
@Serializable
data class DashboardSummaryResponse(
    val user: DashboardUser,
    val financial_summary: FinancialSummary,
    val upcoming_obligations: List<UpcomingObligation>,
    val children: List<ChildDetail>,
    val recent_transactions: List<RecentTransaction>
)

@Serializable
data class DashboardUser(
    val full_name: String,
    val email: String
)

@Serializable
data class FinancialSummary(
    val total_outstanding_balance: Double,
    val total_paid: Double,
    val currency: String
)

@Serializable
data class UpcomingObligation(
    val id: Int,
    val student_name: String,
    val description: String,
    val amount: String,
    val due_date: String,
    val status: String
)

@Serializable
data class ChildDetail(
    val student_id: Int,
    val admission_number: String,
    val first_name: String,
    val last_name: String,
    val school_name: String,
    val student_class: String,
    val date_of_birth: String,
    val outstanding_balance: Double
)

@Serializable
data class RecentTransaction(
    val id: Int,
    val amount: String,
    val method: String,
    val status: String,
    val txn_id: String,
    val created_at: String
)

// --- Notification Models ---
@Serializable
data class UnreadNotificationCountDto(
    val unread_count: Int
)

// --- History & Pagination Models ---
@Serializable
data class PaginatedHistoryResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PaymentHistoryItem>
)

@Serializable
data class PaymentHistoryItem(
    val id: String,
    val amount: String,
    val status: String,
    val created_at: String,
    val item_name: String? = "Obligation",
    val student_name: String? = "Student"
)
@Serializable
data class PaginatedNotificationsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NotificationItemDto>
)

@Serializable
data class NotificationItemDto(
    val id: Int,
    val notification_type: String,
    val title: String,
    val message: String,
    val is_read: Boolean,
    val created_at: String
)
@Serializable
data class PaymentStatusResponse(
    val status: String
)

@Serializable
data class UpdateFcmTokenRequest(
    val fcm_token: String
)