package com.almizan.mobile.data.api

import com.almizan.mobile.data.models.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ================= AUTH =================

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpRequest
    ): Response<LoginResponse> // Note: Backend does not have this endpoint yet - OTP is handled internally

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<User>>
    // Dans ApiService.kt — à ajouter/corriger :
    @GET("users/me")
    suspend fun getMe(): Response<User>  // retourne User directement, pas ApiResponse<User>
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ================= MARCHÉS =================

    @GET("tenders")
    suspend fun getMarches(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("status") statut: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<Marche>>> // Changed to List to match backend [] observed in logs

    @GET("tenders/public")
    suspend fun getMarchesPublic(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Marche>>>

    @GET("tenders/{id}")
    suspend fun getMarcheById(
        @Path("id") id: String
    ): Response<ApiResponse<Marche>>

    @GET("tenders/{id}/cdc/download")
    suspend fun downloadCdc(
        @Path("id") id: String
    ): Response<ApiResponse<ResponseBody>>

    // ================= QUESTIONS =================

    @GET("tenders/{id}/questions")
    suspend fun getQuestions(
        @Path("id") id: String
    ): Response<ApiResponse<List<Question>>>

    @POST("tenders/{id}/questions")
    suspend fun poserQuestion(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<ApiResponse<Question>>
    @POST("tenders/{id}/withdrawals")
    suspend fun retirerCdc(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<ApiResponse<Any>>

    // ================= SOUMISSIONS =================

    @POST("soumissions")
    suspend fun creerSoumission(
        @Body body: Map<String, String>
    ): Response<ApiResponse<Soumission>>

    @GET("soumissions")
    suspend fun getMesSoumissions(): Response<List<Soumission>> // ✅ FIX: Backend returns [] raw array

    @GET("soumissions/{id}")
    suspend fun getSoumission(
        @Path("id") id: String
    ): Response<ApiResponse<Soumission>>

    @POST("soumissions/{id}/submit")
    suspend fun soumettreSoumission(
        @Path("id") id: String
    ): Response<ApiResponse<Soumission>>

    @POST("soumissions/{id}/withdraw")
    suspend fun retirerSoumission(
        @Path("id") id: String
    ): Response<ApiResponse<Soumission>>

    @Multipart
    @POST("soumissions/{id}/admin/attachement")
    suspend fun uploadAdminAttachment(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<Map<String, Any>>>

    @DELETE("soumissions/{id}/admin/attachement/{attachmentId}")
    suspend fun deleteAdminAttachment(
        @Path("id") id: String,
        @Path("attachmentId") attachmentId: String
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("soumissions/{id}/review")
    suspend fun reviewSoumission(
        @Path("id") id: String
    ): Response<ApiResponse<Map<String, Any>>>

    // ================= RECOURS =================

    @POST("recours")
    suspend fun deposerRecours(
        @Body recours: Recours
    ): Response<ApiResponse<Recours>> // Note: Backend does not have this endpoint yet - feature not implemented

    // ================= NOTIFICATIONS =================

    @GET("notifications")
    suspend fun getNotifications(): Response<PaginatedResponse<Notification>>

    @PATCH("notifications/{id}/read") // Updated to PATCH to match backend
    suspend fun marquerLue(
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @PATCH("notifications/read-all") // Updated to PATCH to match backend
    suspend fun marquerToutesLues():
            Response<ApiResponse<Unit>>
}
