package com.example.edupayapp.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.IOException

// --- NEW PERMANENT RAILWAY URLs ---
const val BASE_URL = "https://edupay-backend-production-32f1.up.railway.app/api/accounts/"
const val PAYMENTS_BASE_URL = "https://edupay-backend-production-32f1.up.railway.app/api/payments/"
const val NOTIFICATIONS_BASE_URL = "https://edupay-backend-production-32f1.up.railway.app/api/notifications/"

val tokenStorage: TokenStorage = InMemoryTokenStorage()

val httpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 60000
        connectTimeoutMillis = 60000
        socketTimeoutMillis = 60000
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }

    install(Auth) {
        bearer {
            loadTokens {
                val token = tokenStorage.getToken()
                if (token != null) BearerTokens(token, token) else null
            }
            sendWithoutRequest { request ->
                val path = request.url.encodedPath
                val publicPaths = listOf(
                    "/api/accounts/parent/register/",
                    "/api/accounts/parent/verify-otp/",
                    "/api/accounts/login/"
                )
                if (request.url.host == Url(BASE_URL).host) {
                    !publicPaths.contains(path)
                } else {
                    tokenStorage.getToken() != null
                }
            }
        }
    }

    defaultRequest {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    expectSuccess = true
}

class AuthService {
    // --- 1. Parent Registration ---
    suspend fun registerParent(request: ParentRegisterRequest): AuthMessageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("${BASE_URL}parent/register/") { setBody(request) }
                response.body<AuthMessageResponse>()
            } catch (e: ClientRequestException) {
                val errorBody = try { e.response.body<AuthMessageResponse>() } catch(se: Exception) { null }
                AuthMessageResponse(error = errorBody?.error ?: e.response.status.description)
            } catch (e: Exception) {
                AuthMessageResponse(error = e.message ?: "Network error")
            }
        }
    }

    // --- 2. Parent OTP Verification ---
    suspend fun verifyOtp(request: OtpVerificationRequest): AuthMessageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("${BASE_URL}parent/verify-otp/") { setBody(request) }
                response.body<AuthMessageResponse>()
            } catch (e: ClientRequestException) {
                val errorBody = try { e.response.body<AuthMessageResponse>() } catch(se: Exception) { null }
                AuthMessageResponse(error = errorBody?.error ?: "Verification failed: Invalid OTP.")
            } catch (e: Exception) {
                AuthMessageResponse(error = e.message ?: "Network error")
            }
        }
    }

    // --- 3. Parent PIN Setup ---
    suspend fun setParentPin(request: PinSetupRequest): AuthMessageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("${BASE_URL}parent/set-pin/") { setBody(request) }
                response.body<AuthMessageResponse>()
            } catch (e: ClientRequestException) {
                val errorBody = try { e.response.body<AuthMessageResponse>() } catch(se: Exception) { null }
                AuthMessageResponse(error = errorBody?.error ?: "PIN setup failed.")
            } catch (e: Exception) {
                AuthMessageResponse(error = e.message ?: "Network error")
            }
        }
    }

    // --- 4. Login (Auth) ---
    suspend fun login(request: LoginRequest): LoginResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("${BASE_URL}login/") { setBody(request) }
                val loginData = response.body<LoginResponse>()

                tokenStorage.saveToken(loginData.access_token, loginData.user.email, loginData.user.id)

                loginData
            } catch (e: ClientRequestException) {
                if (e.response.status.value == 401) {
                    throw IllegalStateException("Invalid credentials.")
                }
                val errorBody = try { e.response.body<AuthMessageResponse>() } catch (se: Exception) { null }
                throw IllegalStateException(errorBody?.error ?: e.response.status.description)
            } catch (e: Exception) {
                // Slightly tweaked to catch raw HTML errors more cleanly just in case Railway reboots
                if (e.message?.contains("<!DOCTYPE html>") == true || e.message?.contains("Not enough data") == true) {
                    throw IllegalStateException("Server is currently offline or rebooting. Please try again.")
                }
                throw IllegalStateException(e.message ?: "Login failed. Please check your connection.")
            }
        }
    }

    // --- 5. Logout ---
    suspend fun logout(): AuthMessageResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("${BASE_URL}logout/")
                tokenStorage.clear()
                response.body<AuthMessageResponse>()
            } catch (e: Exception) {
                tokenStorage.clear()
                AuthMessageResponse(error = e.message ?: "Logout failed.")
            }
        }
    }

    // --- 6. Dashboard Data Fetch ---
    suspend fun getDashboardSummary(parentId: String): DashboardSummaryResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("${BASE_URL}parent/dashboard/$parentId/")
                response.body<DashboardSummaryResponse>()
            } catch (e: ClientRequestException) {
                if (e.response.status.value == 401) {
                    throw IllegalStateException("Session expired. Please log in again.")
                }
                val errorBody = try { e.response.body<AuthMessageResponse>() } catch (se: Exception) { null }
                throw IllegalStateException(errorBody?.error ?: "Failed to fetch dashboard data.")
            } catch (e: Exception) {
                throw IllegalStateException("Network error: Could not connect to dashboard API.")
            }
        }
    }

    // --- 7. Initiate Payment ---
    suspend fun initiatePayment(request: InitiatePaymentRequest): InitiatePaymentResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("${PAYMENTS_BASE_URL}initiate-payment/") {
                    setBody(request)
                }
                response.body<InitiatePaymentResponse>()
            } catch (e: ClientRequestException) {
                val errorBody = try { e.response.body<AuthMessageResponse>() } catch (se: Exception) { null }
                throw IllegalStateException(errorBody?.error ?: "Payment initiation failed.")
            } catch (e: Exception) {
                throw IllegalStateException("Network error during payment initiation.")
            }
        }
    }

    // --- 8. Notification Unread Count ---
    suspend fun getUnreadNotificationCount(): UnreadNotificationCountDto {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("${NOTIFICATIONS_BASE_URL}unread-count/")
                response.body<UnreadNotificationCountDto>()
            } catch (e: Exception) {
                UnreadNotificationCountDto(unread_count = 0)
            }
        }
    }

    // --- 9. Paginated Payment History ---
    suspend fun getPaymentHistory(parentId: String, page: Int, statusFilter: String): PaginatedHistoryResponse {
        return withContext(Dispatchers.IO) {
            val url = "${PAYMENTS_BASE_URL}parents/payments/$parentId/?page=$page" +
                    if (statusFilter.isNotBlank()) "&status=$statusFilter" else ""

            httpClient.get(url).body<PaginatedHistoryResponse>()
        }
    }

    // --- 10. Receipt Download ---
    suspend fun downloadReceiptBytes(paymentId: String): ByteArray {
        return withContext(Dispatchers.IO) {
            httpClient.get("${PAYMENTS_BASE_URL}reports/receipts/download/$paymentId/").body<ByteArray>()
        }
    }

    // --- 11. Get Notifications List ---
    suspend fun getNotifications(page: Int = 1): PaginatedNotificationsResponse {
        return withContext(Dispatchers.IO) {
            httpClient.get("${NOTIFICATIONS_BASE_URL}?page=$page&page_size=20").body<PaginatedNotificationsResponse>()
        }
    }

    // --- 12. Mark Notification as Read ---
    suspend fun markNotificationAsRead(notificationId: Int) {
        withContext(Dispatchers.IO) {
            httpClient.patch("${NOTIFICATIONS_BASE_URL}$notificationId/read/")
        }
    }
    // --- 13. Check Specific Payment Status ---
    suspend fun checkPaymentStatus(paymentId: String): PaymentStatusResponse {
        return withContext(Dispatchers.IO) {
            httpClient.get("${PAYMENTS_BASE_URL}status/$paymentId/").body<PaymentStatusResponse>()
        }
    }

    // --- 14. Update FCM Token ---
    suspend fun updateFcmToken(token: String) {
        withContext(Dispatchers.IO) {
            try {
                // We only try to update the token if the user is actually logged in
                if (tokenStorage.getToken() != null) {
                    httpClient.post("${BASE_URL}parent/update-fcm-token/") {
                        setBody(UpdateFcmTokenRequest(token))
                    }
                }
            } catch (e: Exception) {
                // Background network failures here shouldn't crash the app
                println("Failed to update FCM token: ${e.message}")
            }
        }
    }
}