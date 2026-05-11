package com.almizan.mobile.data.models

data class Marche(
    val id: String,
    val service_contractant_id: String = "",
    val version_courante_id: String? = null,
    val visa_id: String? = null,
    val status: String = "PUBLISHED",
    val created_by: String = "",
    val created_at: String = "",
    val updated_at: String = "",
    val version_courante: MarcheVersion? = null,
    val visa: Visa? = null
) {
    fun getTitre() = version_courante?.intitule ?: ""
    fun getReference() = version_courante?.reference ?: ""
    fun resolveStatut() = status ?: "PUBLISHED"  // renamed: getStatut() clashed with val statut

    fun getBudget() = version_courante?.budget_marche_estime?.toDoubleOrNull()
    fun getDateLimite() = version_courante?.date_limite_depot ?: ""
    fun getTypeMarche() = version_courante?.type_marche ?: ""
    fun getModePassation() = version_courante?.mode_passation ?: ""
}

data class MarcheVersion(
    val id: String,
    val ao_id: String = "",
    val version_number: Int = 1,
    val reference: String = "",
    val intitule: String = "",
    val description: String = "",
    val nature_projet: String = "",
    val date_limite_depot: String = "",
    val date_ouverture_plis: String = "",
    val prix_cdc: String? = null,
    val type_marche: String = "",
    val category_marche: String? = null,
    val mode_passation: String = "",
    val type_allotissement: String = "",
    val budget_marche_estime: String? = null,
    val delai_marche: String? = null,
    val cdc_contenu: String? = null,
    val created_at: String = "",
    val updated_at: String = ""
)

data class Visa(
    val id: String,
    val numero_visa: String = "",
    val ao_id: String? = null,
    val demande_visa_id: String, // NOT NULL
    val delivre_par: String,     // NOT NULL
    val delivre_le: String,      // timestamp
    val signature_electronique: String? = null,
    val date_expiration: String? = null,
    val est_actif: Boolean = true, // DEFAULT: true
    val created_at: String = "",
    val updated_at: String = ""
)