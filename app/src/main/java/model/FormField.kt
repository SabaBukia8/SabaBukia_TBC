package model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FormField(
    val fieldId: Int,
    val hint: String,
    val fieldType: FieldType,
    val keyboard: KeyboardType,
    val required: Boolean,
    val isActive: Boolean,
    val icon: String?
) : Parcelable

@Serializable
enum class FieldType {
    INPUT,
    CHOOSER
}

@Serializable
enum class KeyboardType {
    TEXT,
    NUMBER,
    EMAIL
}