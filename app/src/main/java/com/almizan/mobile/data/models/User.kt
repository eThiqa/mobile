package com.almizan.mobile.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,

    @SerializedName("first_name", alternate = ["firstName"])
    val first_name: String = "",

    @SerializedName("last_name", alternate = ["lastName"])
    val last_name: String = "",

    val email: String,
    val role: String,

    @SerializedName("phone_number", alternate = ["phoneNumber"])
    val phone_number: String? = null,

    val gender: String? = null,

    @SerializedName("date_of_birth")
    val date_of_birth: String? = null,

    val wilaya: String? = null,
    val address: String? = null,

    @SerializedName("avatar_url")
    val avatar_url: String? = null,

    val status: String? = null,

    @SerializedName("is_verified")
    val is_verified: Boolean = false,

    @SerializedName("registration_type")
    val registration_type: String? = null,

    @SerializedName("organization_id")
    val organization_id: String? = null,

    @SerializedName("created_at")
    val created_at: String? = null
) {
    val nom get() = last_name
    val prenom get() = first_name
    val nomComplet get() = "$first_name $last_name".trim()
    val initiales get() = buildString {
        if (first_name.isNotEmpty()) append(first_name.first().uppercaseChar())
        if (last_name.isNotEmpty()) append(last_name.first().uppercaseChar())
    }.ifEmpty { "?" }
}