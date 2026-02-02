package com.example.sababukia_tbc.domain.model

data class FormField(
    val id: Int,
    val hint: String,
    val fieldType: FieldType,
    val keyboardType: KeyboardType,
    val isRequired: Boolean,
    val isActive: Boolean,
    val iconUrl: String?
)
