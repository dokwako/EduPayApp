package com.example.edupayapp.data

import kotlinx.serialization.Serializable

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


// --- Responses ---

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