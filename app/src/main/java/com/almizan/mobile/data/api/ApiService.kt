package com.almizan.mobile.data.api

import com.almizan.mobile.data.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>


    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<User>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // --- MARCHÉS (tenders) ---
    @GET("tenders")
    suspend fun getMarches(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("status") statut: String? = null,
        @Query("sector") secteur: String? = null,
        @Query("wilaya") wilaya: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<Marche>>>

    @GET("tenders/{id}")
    suspend fun getMarcheById(@Path("id") id: String): Response<ApiResponse<Marche>>

    @GET("tenders/{id}/cdc/download")
    suspend fun downloadCdc(@Path("id") id: String): Response<okhttp3.ResponseBody>

    // --- QUESTIONS ---
    @GET("tenders/{id}/questions")
    suspend fun getQuestions(@Path("id") id: String): Response<ApiResponse<List<Question>>>

    @POST("tenders/{id}/questions")
    suspend fun poserQuestion(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<ApiResponse<Question>>

    // --- SOUMISSIONS ---
    @GET("soumissions/my")
    suspend fun getMesSoumissions(): Response<ApiResponse<List<Soumission>>>

    @GET("soumissions/{id}/scores")
    suspend fun getNotesDetail(@Path("id") id: String): Response<ApiResponse<Map<String, Any>>>

    @Multipart
    @POST("soumissions/{marcheId}/enveloppe-admin")
    suspend fun uploadEnveloppeAdmin(
        @Path("marcheId") marcheId: String,
        @Part files: List<MultipartBody.Part>
    ): Response<ApiResponse<Map<String, Any>>>

    @Multipart
    @POST("soumissions/{marcheId}/enveloppe-technique")
    suspend fun uploadEnveloppeTechnique(
        @Path("marcheId") marcheId: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<Map<String, Any>>>

    @Multipart
    @POST("soumissions/{marcheId}/enveloppe-financiere")
    suspend fun uploadEnveloppeFinanciere(
        @Path("marcheId") marcheId: String,
        @Part file: MultipartBody.Part,
        @Part("montant") montant: okhttp3.RequestBody
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("soumissions/{marcheId}/confirm")
    suspend fun confirmerSoumission(
        @Path("marcheId") marcheId: String
    ): Response<ApiResponse<Soumission>>

    // --- RECOURS ---
    @POST("recours")
    suspend fun deposerRecours(@Body recours: Recours): Response<ApiResponse<Recours>>

    // --- NOTIFICATIONS ---
    @GET("notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<Notification>>>

    @POST("notifications/{id}/read")
    suspend fun marquerLue(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("notifications/read-all")
    suspend fun marquerToutesLues(): Response<ApiResponse<Unit>>

    // --- IA ---
    @POST("ia/validate-admin-dossier")
    suspend fun validerDossierAdmin(
        @Body body: Map<String, String>
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("ia/price-estimate/{marcheId}")
    suspend fun getEstimationPrix(
        @Path("marcheId") marcheId: String
    ): Response<ApiResponse<Map<String, Any>>>
    // Add this — no auth needed
    @GET("tenders")
    suspend fun getMarches(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("status") statut: String? = null,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<Marche>>

    @GET("tenders/public")
    suspend fun getMarchesPublic(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<Marche>>
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<ApiResponse<OtpResponse>>
}