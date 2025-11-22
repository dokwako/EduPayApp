import kotlinx.serialization.Serializable

// --- Storage Interface ---
interface TokenStorage {
    fun saveToken(token: String, email: String)
    fun getToken(): String?
    fun getEmail(): String?
    fun clear()
}

// In-Memory implementation for testing
class InMemoryTokenStorage : TokenStorage {
    private var accessToken: String? = null
    private var userEmail: String? = null

    override fun saveToken(token: String, email: String) {
        accessToken = token
        userEmail = email
    }

    override fun getToken(): String? = accessToken
    override fun getEmail(): String? = userEmail
    override fun clear() {
        accessToken = null
        userEmail = null
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