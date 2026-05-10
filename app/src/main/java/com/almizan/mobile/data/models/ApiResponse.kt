package com.almizan.mobile.data.models

data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)

data class LoginResponse(
    val accessToken: String? = null,
    val token: String? = null,
    val refreshToken: String? = null,
    val requiresOtp: Boolean = false,
    val user: User? = null
) {
    fun resolveToken() = accessToken ?: token ?: ""  // renamed: getToken() clashed with val token
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class OtpRequest(
    val email: String,
    val otp: String
)

data class OtpResponse(
    val token: String,
    val user: User
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val gender: String = "MALE",
    val registration_type: String = "SELF_REGISTERED"
)

data class PaginatedResponse<T>(
    val success: Boolean = true,
    val data: List<T> = emptyList(),
    val meta: PaginationMeta? = null
)

data class PaginationMeta(
    val pagination: Pagination? = null
)

data class Pagination(
    val page: Int = 1,
    val limit: Int = 20,
    val total_items: Int = 0,
    val total_pages: Int = 0,
    val has_next: Boolean = false,
    val has_prev: Boolean = false
)

data class Recours(
    val id: String = "",
    val soumissionId: String,
    val motif: String,
    val contenu: String,
    val pieceJointe: String? = null,
    val dateDepot: String? = null,
    val statut: String? = "EN_ATTENTE"
)

data class Notification(
    val id: String,
    val titre: String? = null,
    val title: String? = null,
    val corps: String? = null,
    val message: String? = null,   // clashes with getMessage()
    val type: String,
    val date: String? = null,
    val created_at: String? = null,
    val lue: Boolean = false,
    val read: Boolean = false,
    val marcheId: String? = null
) {
    fun resolveTitle() = titre ?: title ?: ""      // renamed: getTitle() clashed with val title
    fun resolveMessage() = corps ?: message ?: ""  // renamed: getMessage() clashed with val message
    fun isRead() = lue || read
}