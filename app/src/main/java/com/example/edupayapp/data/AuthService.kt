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
import kotlinx.serialization.json.Json
import java.io.IOException

// --- FIX: REMOVED REDUNDANT IMPORTS ---
// I removed the explicit imports for AuthMessageResponse, LoginRequest, etc.
// Because this file is in 'package com.example.edupayapp.data', it automatically
// sees everything in AuthModels.kt. Importing them explicitly causes errors.
// -----------------------------------------------------------

const val BASE_URL = "https://edupay-backend-pj75.onrender.com/api/accounts/"
const val PAYMENTS_BASE_URL = "https://edupay-backend-pj75.onrender.com/api/payments/"

// NOTE: tokenStorage and InMemoryTokenStorage are assumed to be defined in AuthModels.kt
val tokenStorage: TokenStorage = InMemoryTokenStorage()

// Global Ktor HttpClient Setup
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
        return try {
            // FIX: String concatenation prevents Ktor from erasing the /api/accounts/ path
            val response = httpClient.post("${BASE_URL}parent/register/") { setBody(request) }
            response.body<AuthMessageResponse>()
        } catch (e: ClientRequestException) {
            val errorBody = e.response.body<AuthMessageResponse>()
            AuthMessageResponse(error = errorBody.error ?: e.response.status.description)
        } catch (e: Exception) {
            AuthMessageResponse(error = e.message ?: "Network error")
        }
    }

    // --- 2. Parent OTP Verification ---
    suspend fun verifyOtp(request: OtpVerificationRequest): AuthMessageResponse {
        return try {
            val response = httpClient.post("${BASE_URL}parent/verify-otp/") { setBody(request) }
            response.body<AuthMessageResponse>()
        } catch (e: ClientRequestException) {
            val errorBody = e.response.body<AuthMessageResponse>()
            AuthMessageResponse(error = errorBody.error ?: "Verification failed: Invalid OTP.")
        } catch (e: Exception) {
            AuthMessageResponse(error = e.message ?: "Network error")
        }
    }

    // --- 3. Parent PIN Setup ---
    suspend fun setParentPin(request: PinSetupRequest): AuthMessageResponse {
        return try {
            val response = httpClient.post("${BASE_URL}parent/set-pin/") { setBody(request) }
            response.body<AuthMessageResponse>()
        } catch (e: ClientRequestException) {
            val errorBody = e.response.body<AuthMessageResponse>()
            AuthMessageResponse(error = errorBody.error ?: "PIN setup failed.")
        } catch (e: Exception) {
            AuthMessageResponse(error = e.message ?: "Network error")
        }
    }

    // --- 4. Login (Auth) ---
    suspend fun login(request: LoginRequest): LoginResponse {
        return try {
            val response = httpClient.post("${BASE_URL}login/") { setBody(request) }
            val loginData = response.body<LoginResponse>()

            tokenStorage.saveToken(loginData.access_token, loginData.user.email, loginData.user.id)

            return loginData
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 401) {
                throw IllegalStateException("Invalid credentials.")
            }
            val errorBody = try { e.response.body<AuthMessageResponse>() } catch (se: Exception) { null }
            throw IllegalStateException(errorBody?.error ?: e.response.status.description)
        } catch (e: Exception) {
            if (e.message?.contains("Not enough data available") == true || e.message?.contains("End of input") == true || e is IOException) {
                throw IllegalStateException("Login failed: Server returned incomplete data. Please notify the backend team.")
            }
            throw e
        }
    }

    // --- 5. Logout ---
    suspend fun logout(): AuthMessageResponse {
        return try {
            val response = httpClient.post("${BASE_URL}logout/")
            tokenStorage.clear()
            response.body<AuthMessageResponse>()
        } catch (e: Exception) {
            tokenStorage.clear()
            AuthMessageResponse(error = e.message ?: "Logout failed.")
        }
    }

    // --- 6. Dashboard Data Fetch ---
    suspend fun getDashboardSummary(parentId: String): DashboardSummaryResponse {
        return try {
            val response = httpClient.get("${BASE_URL}parent/dashboard/$parentId/")
            return response.body<DashboardSummaryResponse>()
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

    // --- 7. Initiate Payment ---
    suspend fun initiatePayment(request: InitiatePaymentRequest): InitiatePaymentResponse {
        return try {
            // Note: Uses PAYMENTS_BASE_URL here instead of BASE_URL
            val response = httpClient.post("${PAYMENTS_BASE_URL}initiate-payment/") {
                setBody(request)
            }
            return response.body<InitiatePaymentResponse>()
        } catch (e: ClientRequestException) {
            val errorBody = try { e.response.body<AuthMessageResponse>() } catch (se: Exception) { null }
            throw IllegalStateException(errorBody?.error ?: "Payment initiation failed: Check phone number/obligation.")
        } catch (e: Exception) {
            throw IllegalStateException("Network error during payment initiation.")
        }
    }
}