package com.almizan.mobile.data.models

data class User(
    val id: String,
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val role: String, // "OE", "SC", "CIM", "COE", "ADM"
    val raisonSociale: String? = null,
    val registreCommerce: String? = null,
    val token: String? = null
)