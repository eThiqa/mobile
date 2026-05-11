package com.almizan.mobile.data.models

data class Question(
    val id: String,
    val ao_id: String = "",
    val asked_by_user_id: String? = null,
    val question_text: String = "",
    val answer_text: String? = null,
    val answered_by_user_id : String? = null,
val answered_at: String? = null,
    val published_at: String? = null,
    val created_at: String = "",
    val updated_at: String = ""

)