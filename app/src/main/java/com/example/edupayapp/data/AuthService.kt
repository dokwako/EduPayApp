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

const val BASE_URL = "https://edupay-backend-pj75.onrender.com/api/accounts/"

// NOTE: tokenStorage must be defined in AuthModels.kt
val tokenStorage: TokenStorage = InMemoryTokenStorage()

// Global Ktor HttpClient Setup
val httpClient = HttpClient(CIO) {
    // Install Timeouts (Critical for Render's cold start)
    install(HttpTimeout) {
        requestTimeoutMillis = 60000 // 60 seconds
        connectTimeoutMillis = 60000
        socketTimeoutMillis = 60000
    }

    // Install JSON serialization plugin
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }

    // Install Auth plugin for automatically attaching the token
    install(Auth) {
        bearer {
            loadTokens {
                // Get the token from our storage
                val token = tokenStorage.getToken()
                if (token != null) BearerTokens(token, token) else null
            }
            // FIX: The lambda receives an HttpRequestBuilder. Access its 'url' property.
            sendWithoutRequest { request ->
                request.url.host == Url(BASE_URL).host
            }
        }
    }

    // Default request configuration
    defaultRequest {
        url(BASE_URL)
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    // Error handling
    expectSuccess = true
}

class AuthService {
    // --- 1. Parent Registration ---
    suspend fun registerParent(request: ParentRegisterRequest): AuthMessageResponse {
        return try {
            val response = httpClient.post("parent/register/") {
                setBody(request)
            }
            response.body<AuthMessageResponse>()
        } catch (e: ClientRequestException) {
            // Handle 400 Bad Request error response
            val errorBody = e.response.body<AuthMessageResponse>()
            AuthMessageResponse(error = errorBody.error ?: e.response.status.description)
        } catch (e: Exception) {
            AuthMessageResponse(error = e.message ?: "Network error")
        }
    }

    // --- 2. Parent OTP Verification ---
    suspend fun verifyOtp(request: OtpVerificationRequest): AuthMessageResponse {
        return try {
            val response = httpClient.post("parent/verify-otp/") {
                setBody(request)
            }
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
            val response = httpClient.post("parent/set-pin/") {
                setBody(request)
            }
            response.body<AuthMessageResponse>()
        } catch (e: ClientRequestException) {
            val errorBody = e.response.body<AuthMessageResponse>()
            AuthMessageResponse(error = errorBody.error ?: "PIN setup failed.")
        } catch (e: Exception) {
            AuthMessageResponse(error = e.message ?: "Network error")
        }
    }

    // --- 4. Login (Saves Token and Email) ---
    suspend fun login(request: LoginRequest): LoginResponse {
        return try {
            val response = httpClient.post("login/") {
                setBody(request)
            }
            val loginData = response.body<LoginResponse>()

            // CRITICAL STEP: Save token and email on success
            tokenStorage.saveToken(loginData.access_token, loginData.user.email)

            return loginData
        } catch (e: ClientRequestException) {
            // This catches HTTP errors (401, 404, etc.)
            if (e.response.status.value == 401) {
                throw IllegalStateException("Invalid credentials.")
            }
            // For other client errors, try to extract the message
            val errorBody = try { e.response.body<AuthMessageResponse>() } catch (se: Exception) { null }
            throw IllegalStateException(errorBody?.error ?: e.response.status.description)
        } catch (e: Exception) {
            // FIX: Catch Serialization/IO errors here
            if (e.message?.contains("Not enough data available") == true || e.message?.contains("End of input") == true || e is IOException) {
                // This is the serialization failure. Server sent bad/empty JSON body.
                throw IllegalStateException("Login failed: Server returned incomplete data. Please notify the backend team.")
            }
            throw e
        }
    }
}