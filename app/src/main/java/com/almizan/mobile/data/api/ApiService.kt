package com.almizan.mobile.data.api

import com.almizan.mobile.data.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<User>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // --- MARCHÉS ---
    @GET("marches")
    suspend fun getMarches(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("statut") statut: String? = null,
        @Query("secteur") secteur: String? = null,
        @Query("wilaya") wilaya: String? = null,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<Marche>>>

    @GET("marches/{id}")
    suspend fun getMarcheById(@Path("id") id: String): Response<ApiResponse<Marche>>

    @GET("marches/{id}/cdc")
    suspend fun downloadCdc(@Path("id") id: String): Response<okhttp3.ResponseBody>

    // --- QUESTIONS ---
    @GET("marches/{id}/questions")
    suspend fun getQuestions(@Path("id") id: String): Response<ApiResponse<List<Question>>>

    @POST("marches/{id}/questions")
    suspend fun poserQuestion(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<ApiResponse<Question>>

    // --- SOUMISSIONS ---
    @GET("soumissions/mes-soumissions")
    suspend fun getMesSoumissions(): Response<ApiResponse<List<Soumission>>>

    @GET("soumissions/{id}/notes")
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

    @POST("soumissions/{marcheId}/confirmer")
    suspend fun confirmerSoumission(
        @Path("marcheId") marcheId: String
    ): Response<ApiResponse<Soumission>>

    // --- RECOURS ---
    @POST("recours")
    suspend fun deposerRecours(@Body recours: Recours): Response<ApiResponse<Recours>>

    // --- NOTIFICATIONS ---
    @GET("notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<Notification>>>

    @PUT("notifications/{id}/lire")
    suspend fun marquerLue(@Path("id") id: String): Response<ApiResponse<Unit>>

    @PUT("notifications/lire-toutes")
    suspend fun marquerToutesLues(): Response<ApiResponse<Unit>>

    // --- IA ---
    @POST("ia/valider-dossier-admin")
    suspend fun validerDossierAdmin(
        @Body body: Map<String, String>
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("ia/estimation-prix/{marcheId}")
    suspend fun getEstimationPrix(
        @Path("marcheId") marcheId: String
    ): Response<ApiResponse<Map<String, Any>>>
}