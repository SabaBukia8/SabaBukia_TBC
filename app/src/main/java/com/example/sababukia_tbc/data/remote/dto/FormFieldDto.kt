package com.example.sababukia_tbc.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class FormFieldDto(
    @SerialName("field_id")
    val fieldId: Int,
    val hint: String,
    @SerialName("field_type")
    val fieldType: String,
    val keyboard: String? = null,
    val required: JsonPrimitive,
    @SerialName("is_active")
    val isActive: Boolean,
    val icon: String? = null
)
