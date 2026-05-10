package com.almizan.mobile.data.models

data class User(
    val id: String,
    val first_name: String = "",
    val last_name: String = "",
    val email: String,
    val role: String,
    val phone_number: String? = null,
    val organization_id: String? = null
) {
    val nom get() = last_name
    val prenom get() = first_name
}