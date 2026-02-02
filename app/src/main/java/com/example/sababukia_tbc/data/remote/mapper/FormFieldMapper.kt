package com.example.sababukia_tbc.data.remote.mapper

import com.example.sababukia_tbc.data.remote.dto.FormFieldDto
import com.example.sababukia_tbc.domain.model.FieldType
import com.example.sababukia_tbc.domain.model.FormField
import com.example.sababukia_tbc.domain.model.FormSection
import com.example.sababukia_tbc.domain.model.KeyboardType
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull

fun List<List<FormFieldDto>>.toFormSections(): List<FormSection> {
    return mapIndexed { index, fieldsDto ->
        FormSection(
            id = index,
            fields = fieldsDto.map { it.toFormField() }
        )
    }
}

fun FormFieldDto.toFormField(): FormField {
    return FormField(
        id = fieldId,
        hint = hint,
        fieldType = fieldType.toFieldType(),
        keyboardType = keyboard.toKeyboardType(),
        isRequired = required.toBoolean(),
        isActive = isActive,
        iconUrl = icon
    )
}

private fun String.toFieldType(): FieldType {
    return when (lowercase()) {
        "chooser" -> FieldType.CHOOSER
        else -> FieldType.INPUT
    }
}

private fun String?.toKeyboardType(): KeyboardType {
    return when (this?.lowercase()) {
        "number" -> KeyboardType.NUMBER
        "email" -> KeyboardType.EMAIL
        else -> KeyboardType.TEXT
    }
}

private fun JsonPrimitive.toBoolean(): Boolean {
    return booleanOrNull ?: (content.lowercase() == "true")
}
