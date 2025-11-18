package com.example.edupayapp.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


const val BASE_URL = "https://edupay-backend.onrender.com/api/accounts/"

// Global Ktor HttpClient Setup
val httpClient = HttpClient(CIO) {
    // Install JSON serialization plugin
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // Ignore fields we don't define in models
            prettyPrint = true
            isLenient = true
        })
    }

    // Default request configuration
    defaultRequest {
        url(BASE_URL)
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }

    // Error handling
    expectSuccess = true // Throw exceptions on 4xx and 5xx responses
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
            // Handle 400 Bad Request error response (e.g., "Email already exists")
            AuthMessageResponse(error = e.response.status.description)
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
            // Handle 400 Bad Request error response (e.g., "Invalid OTP")
            AuthMessageResponse(error = "Verification failed: Invalid OTP or network issue.")
        } catch (e: Exception) {
            AuthMessageResponse(error = e.message ?: "Network error")
        }
    }

    // --- 3. Parent PIN Setup ---
    suspend fun setParentPin(request: PinSetupRequest): AuthMessageResponse {
        // Implementation needed for Step 3
        return AuthMessageResponse(message = "Pin Setup not yet implemented in service.")
    }

    // --- 4. Login ---
    suspend fun login(request: LoginRequest): LoginResponse {
        // Implementation needed for Login
        throw NotImplementedError("Login not yet implemented in service.")
    }
}