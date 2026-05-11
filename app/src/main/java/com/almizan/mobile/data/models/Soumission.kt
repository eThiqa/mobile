package com.almizan.mobile.data.models


import com.google.gson.annotations.SerializedName

data class AoInfo(
    val id: String = "",
    val title: String? = null,
    val reference: String? = null,
    @SerializedName("submission_deadline")
    val submission_deadline: String? = null
)

data class Soumission(
    val id: String,
    val ao_id: String = "",
    val status: String = "DRAFT",
    val submitted_at: String? = null,
    val created_at: String? = null,
    val envelopes: List<Envelope>? = null,
    val ao: AoInfo? = null
) {
    val marcheTitre: String get() = ao?.title ?: ao_id
    val dateDepot: String get() = submitted_at ?: created_at ?: ""
}


data class Envelope(
    val id: String = "",
    val type: String = "",
    val status: String = "",
    val attachments: List<Attachment>? = null
)

data class Attachment(
    val id: String = "",
    val filename: String = "",
    val url: String? = null,
    val uploaded_at: String = ""
)