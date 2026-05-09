package com.almizan.mobile.data.models

data class Soumission(
    val id: String,
    val marcheId: String,
    val marcheTitre: String,
    val dateDepot: String,
    val statut: SoumissionStatut,
    val noteAdministrative: Double? = null,
    val noteTechnique: Double? = null,
    val noteFinanciere: Double? = null,
    val noteGlobale: Double? = null,
    val rang: Int? = null,
    val recoursPossible: Boolean = false,
    val recoursDepose: Boolean = false
)

enum class SoumissionStatut {
    BROUILLON,
    DEPOSEE,
    EN_EVALUATION,
    ELIMINEE_ADMIN,
    ELIMINEE_TECHNIQUE,
    NOTEE,
    ATTRIBUTAIRE,
    PERDANTE
}