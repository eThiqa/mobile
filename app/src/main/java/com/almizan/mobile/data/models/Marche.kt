package com.almizan.mobile.data.models

data class Marche(
    val id: String,
    val reference: String,
    val titre: String,
    val serviceContractant: String,
    val wilaya: String,
    val typeMarche: String,       // "FOURNITURES", "TRAVAUX", "SERVICES"
    val modePassation: String,    // "AO_OUVERT", "AO_RESTREINT", "GRE_A_GRE"
    val budgetEstimatif: Double?,
    val datePublication: String,
    val dateLimiteRetrait: String,
    val dateLimiteSoumission: String,
    val statut: MarcheStatut,
    val secteurActivite: String,
    val cdcDisponible: Boolean = true
)

enum class MarcheStatut {
    EN_COURS,
    CLOTURE,
    EVALUATION,
    ATTRIBUE_PROVISOIRE,
    ATTRIBUE_DEFINITIF,
    ANNULE
}