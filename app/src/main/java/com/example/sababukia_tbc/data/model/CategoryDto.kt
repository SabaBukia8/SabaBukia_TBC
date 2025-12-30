package com.example.sababukia_tbc.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("name_de")
    val nameDe: String? = null,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("order_id")
    val orderId: Int? = null,
    @SerialName("children")
    val children: List<CategoryDto> = emptyList()
)
