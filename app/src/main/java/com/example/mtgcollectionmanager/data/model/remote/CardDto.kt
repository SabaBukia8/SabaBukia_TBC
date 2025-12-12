package com.example.mtgcollectionmanager.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("mana_cost") val manaCost: String? = null,
    @SerialName("image_uris") val imageUris: ImageUrisDto? = null,
    @SerialName("type_line") val typeLine: String,
    @SerialName("rarity") val rarity: String,
    @SerialName("set") val setCode: String,
    @SerialName("set_name") val setName: String,
    @SerialName("collector_number") val collectorNumber: String? = null,
    @SerialName("released_at") val releasedAt: String? = null,
    @SerialName("colors") val colors: List<String>? = null,
    @SerialName("prices") val prices: PricesDto? = null
)

@Serializable
data class ImageUrisDto(
    @SerialName("normal") val normal: String? = null,
    @SerialName("large") val large: String? = null,
    @SerialName("png") val png: String? = null
)

@Serializable
data class PricesDto(
    @SerialName("usd") val usd: String? = null
)
