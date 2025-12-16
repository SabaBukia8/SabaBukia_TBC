package com.example.mtgcollectionmanager.presentation.mapper

import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.presentation.model.CardUiModel
import com.example.mtgcollectionmanager.presentation.model.CollectionCardUiModel
import com.example.mtgcollectionmanager.presentation.model.MarketPriceUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Card.toUi(): CardUiModel {

    val defaultPrice = pricing.markets.firstOrNull { it.normalPrice != null }?.normalPrice ?: 0.0
    

    val marketPricesUi = pricing.markets.map { market ->
        MarketPriceUiModel(
            marketName = market.marketName,
            normalPrice = market.normalPrice?.let { String.format("\$%.2f", it) } ?: "N/A",
            foilPrice = market.foilPrice?.let { String.format("\$%.2f", it) } ?: "N/A",
            purchaseUrl = market.purchaseUrl
        )
    }
    
    return CardUiModel(
        cardId = id,
        name = name,
        manaCost = manaCost.ifEmpty { "N/A" },
        imageUrl = imageUrl,
        type = type,
        rarity = rarity.replaceFirstChar { it.uppercase() },
        setCode = setCode,
        setName = setName,
        collectorNumber = collectorNumber,
        releasedAt = releasedAt,
        setInfo = "$setCode - $setName",
        colorsDisplay = if (colors.isEmpty()) "Colorless" else colors.joinToString(", "),
        priceFormatted = if (defaultPrice > 0) String.format("\$%.2f", defaultPrice) else "N/A",
        marketPrices = marketPricesUi
    )
}

fun CollectionCard.toUi(): CollectionCardUiModel = with(card) {
    val defaultPrice = pricing.markets.firstOrNull { it.normalPrice != null }?.normalPrice ?: 0.0
    
    CollectionCardUiModel(
        cardId = cardId,
        name = name,
        imageUrl = imageUrl,
        setInfo = "$setCode - $setName",
        setCode = setCode,
        colors = colors,
        quantity = this@toUi.quantity,
        quantityDisplay = "x${this@toUi.quantity}",
        condition = this@toUi.condition.name.lowercase().split("_")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
        priceFormatted = String.format("\$%.2f", defaultPrice),
        totalValueFormatted = String.format("\$%.2f", defaultPrice * this@toUi.quantity),
        addedDateFormatted = formatDate(this@toUi.addedDate),
        notes = this@toUi.notes
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return "Added ${sdf.format(Date(timestamp))}"
}
