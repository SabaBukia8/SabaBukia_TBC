package com.example.mtgcollectionmanager.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetsResponseDto(
    @SerialName("data")
    val data: List<SetDto> = emptyList()
)

@Serializable
data class SetDto(
    @SerialName("id")
    val id: String,
    @SerialName("code")
    val code: String,
    @SerialName("name")
    val name: String,
    @SerialName("released_at")
    val releasedAt: String? = null,
    @SerialName("set_type")
    val setType: String? = null,
    @SerialName("card_count")
    val cardCount: Int = 0,
    @SerialName("icon_svg_uri")
    val iconSvgUri: String? = null
)
