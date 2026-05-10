package com.almizan.mobile.data.models

data class Soumission(
    val id: String,
    val marcheId: String? = null,
    val ao_id: String? = null,
    val marcheTitre: String? = null,
    val dateDepot: String? = null,
    val created_at: String? = null,
    val statut: String? = null,
    val status: String? = null,
    val noteAdministrative: Double? = null,
    val noteTechnique: Double? = null,
    val noteFinanciere: Double? = null,
    val noteGlobale: Double? = null,
    val rang: Int? = null,
    val recoursPossible: Boolean = false,
    val recoursDepose: Boolean = false
) {
    fun resolveId() = ao_id ?: marcheId ?: ""          // renamed: getMarcheId() clashed with val marcheId
    fun resolveStatut() = status ?: statut ?: "BROUILLON"  // renamed: getStatut() clashed with val statut
    fun resolveDate() = dateDepot ?: created_at ?: ""      // renamed: getDateDepot() clashed with val dateDepot
}

enum class SoumissionStatut {
    BROUILLON, DEPOSEE, EN_EVALUATION,
    ELIMINEE_ADMIN, ELIMINEE_TECHNIQUE,
    NOTEE, ATTRIBUTAIRE, PERDANTE
}