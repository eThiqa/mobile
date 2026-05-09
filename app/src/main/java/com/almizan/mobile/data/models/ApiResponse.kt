package com.almizan.mobile.data.models
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User,
    val requiresOtp: Boolean
)

data class OtpRequest(
    val email: String,
    val otp: String
)

// ✅ CORRECT — confirmPassword n'appartient pas au modèle API
data class RegisterRequest(
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val password: String,
    val raisonSociale: String,
    val registreCommerce: String,
    val secteurActivite: String
)

data class Question(
    val id: String,
    val marcheId: String,
    val contenu: String,
    val reponse: String?,
    val dateQuestion: String,
    val dateReponse: String?,
    val estPublique: Boolean
)

data class Recours(
    val id: String,
    val soumissionId: String,
    val motif: String,
    val contenu: String,
    val pieceJointe: String?,
    val dateDepot: String?,
    val statut: String? = "EN_ATTENTE"
)

data class Notification(
    val id: String,
    val titre: String,
    val corps: String,
    val type: String, // "NOUVEAU_MARCHE", "REPONSE_QUESTION", "RESULTAT", "RECOURS"
    val date: String,
    val lue: Boolean,
    val marcheId: String?
)