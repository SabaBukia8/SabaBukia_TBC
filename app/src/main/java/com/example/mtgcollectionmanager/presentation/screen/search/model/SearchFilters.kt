package com.example.mtgcollectionmanager.presentation.screen.search.model

data class SearchFilters(
    // Color filters (include/exclude colors)
    val colors: Map<String, ColorFilterState> = emptyMap(),

    // Set filters
    val sets: Set<String> = emptySet(),

    // Card types
    val cardTypes: Set<String> = emptySet(),

    // Rarity
    val rarities: Set<String> = emptySet(),

    // Mana value range
    val manaValueMin: Int? = null,
    val manaValueMax: Int? = null,

    // Price range (in USD)
    val priceMin: Float? = null,
    val priceMax: Float? = null,

    // Artist name
    val artist: String = "",

    // Year released
    val yearMin: Int? = null,
    val yearMax: Int? = null,

    // Additional text filters
    val oracleText: String = "",
    val flavorText: String = ""
) {
    fun isEmpty(): Boolean {
        return colors.isEmpty() &&
                sets.isEmpty() &&
                cardTypes.isEmpty() &&
                rarities.isEmpty() &&
                manaValueMin == null &&
                manaValueMax == null &&
                priceMin == null &&
                priceMax == null &&
                artist.isBlank() &&
                yearMin == null &&
                yearMax == null &&
                oracleText.isBlank() &&
                flavorText.isBlank()
    }

    fun toScryfallQuery(): String {
        val queryParts = mutableListOf<String>()

        // Color filters
        val includeColors = colors.filter { it.value == ColorFilterState.INCLUDE }.keys
        val excludeColors = colors.filter { it.value == ColorFilterState.EXCLUDE }.keys

        if (includeColors.isNotEmpty()) {
            queryParts.add("c:${includeColors.joinToString("")}")
        }
        if (excludeColors.isNotEmpty()) {
            excludeColors.forEach { color ->
                queryParts.add("-c:$color")
            }
        }

        // Set filters
        if (sets.isNotEmpty()) {
            if (sets.size == 1) {
                queryParts.add("set:${sets.first()}")
            } else {
                queryParts.add("(${sets.joinToString(" or ") { "set:$it" }})")
            }
        }

        // Card types
        if (cardTypes.isNotEmpty()) {
            cardTypes.forEach { type ->
                queryParts.add("type:$type")
            }
        }

        // Rarity
        if (rarities.isNotEmpty()) {
            if (rarities.size == 1) {
                queryParts.add("rarity:${rarities.first()}")
            } else {
                queryParts.add("(${rarities.joinToString(" or ") { "rarity:$it" }})")
            }
        }

        // Mana value range
        if (manaValueMin != null && manaValueMax != null) {
            queryParts.add("mv>=$manaValueMin mv<=$manaValueMax")
        } else if (manaValueMin != null) {
            queryParts.add("mv>=$manaValueMin")
        } else if (manaValueMax != null) {
            queryParts.add("mv<=$manaValueMax")
        }

        // Price range
        if (priceMin != null && priceMax != null) {
            queryParts.add("usd>=$priceMin usd<=$priceMax")
        } else if (priceMin != null) {
            queryParts.add("usd>=$priceMin")
        } else if (priceMax != null) {
            queryParts.add("usd<=$priceMax")
        }

        // Artist
        if (artist.isNotBlank()) {
            queryParts.add("artist:\"$artist\"")
        }

        // Year range
        if (yearMin != null && yearMax != null) {
            queryParts.add("year>=$yearMin year<=$yearMax")
        } else if (yearMin != null) {
            queryParts.add("year>=$yearMin")
        } else if (yearMax != null) {
            queryParts.add("year<=$yearMax")
        }

        // Oracle text (rules text)
        if (oracleText.isNotBlank()) {
            queryParts.add("oracle:\"$oracleText\"")
        }

        // Flavor text
        if (flavorText.isNotBlank()) {
            queryParts.add("flavor:\"$flavorText\"")
        }

        return queryParts.joinToString(" ")
    }
}

enum class ColorFilterState {
    NEUTRAL,
    INCLUDE,
    EXCLUDE
}
