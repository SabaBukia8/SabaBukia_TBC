package com.example.mtgcollectionmanager.data.mapper

import com.example.mtgcollectionmanager.data.model.local.CollectionCardEntity
import com.example.mtgcollectionmanager.data.model.remote.CardDto
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CardPricing
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import com.example.mtgcollectionmanager.domain.model.MarketPrice
import kotlinx.serialization.json.Json

fun CardDto.toDomain(): Card = Card(
    id = id,
    name = name,
    manaCost = manaCost ?: "",
    imageUrl = imageUris?.normal ?: imageUris?.large ?: imageUris?.png ?: "",
    type = typeLine,
    rarity = rarity,
    setCode = setCode,
    setName = setName,
    collectorNumber = collectorNumber ?: "",
    releasedAt = releasedAt ?: "",
    colors = colors ?: emptyList(),
    pricing = mapPricing(prices?.usd, prices?.usdFoil, prices?.eur, prices?.eurFoil, purchaseUris)
)

private fun mapPricing(
    usd: String?,
    usdFoil: String?,
    eur: String?,
    eurFoil: String?,
    purchaseUris: Map<String, String>?
): CardPricing {
    val markets = mutableListOf<MarketPrice>()


    markets.add(
        MarketPrice(
            marketName = "TCGPlayer",
            marketId = MarketPrice.MARKET_TCGPLAYER,
            normalPrice = usd?.toDoubleOrNull(),
            foilPrice = usdFoil?.toDoubleOrNull(),
            purchaseUrl = purchaseUris?.get("tcgplayer")
        )
    )


    markets.add(
        MarketPrice(
            marketName = "Card Kingdom",
            marketId = MarketPrice.MARKET_CARD_KINGDOM,
            normalPrice = null,
            foilPrice = null,
            purchaseUrl = purchaseUris?.get("cardkingdom")
        )
    )


    markets.add(
        MarketPrice(
            marketName = "CardMarket",
            marketId = MarketPrice.MARKET_CARDMARKET,
            normalPrice = eur?.toDoubleOrNull(),
            foilPrice = eurFoil?.toDoubleOrNull(),
            purchaseUrl = purchaseUris?.get("cardmarket")
        )
    )


    markets.add(
        MarketPrice(
            marketName = "StarCityGames",
            marketId = MarketPrice.MARKET_STAR_CITY_GAMES,
            normalPrice = null,
            foilPrice = null,
            purchaseUrl = null
        )
    )

    return CardPricing(markets)
}

fun Card.toEntity(
    collectionId: Long,
    categoryId: Long?,
    quantity: Int,
    condition: CardCondition,
    addedDate: Long,
    notes: String,
    userId: String
): CollectionCardEntity = CollectionCardEntity(
    collectionId = collectionId,
    categoryId = categoryId,
    cardId = id,
    name = name,
    manaCost = manaCost,
    imageUrl = imageUrl,
    type = type,
    rarity = rarity,
    setCode = setCode,
    setName = setName,
    colorsJson = Json.encodeToString(colors),
    price = pricing.markets.firstOrNull()?.normalPrice ?: 0.0,
    quantity = quantity,
    condition = condition.name,
    addedDate = addedDate,
    notes = notes,
    userId = userId
)
//gavyot entity mapperebi
fun CollectionCardEntity.toDomain(): CollectionCard = CollectionCard(
    cardId = cardId,
    card = Card(
        id = cardId,
        name = name,
        manaCost = manaCost,
        imageUrl = imageUrl,
        type = type,
        rarity = rarity,
        setCode = setCode,
        setName = setName,
        collectorNumber = "",
        releasedAt = "",
        colors = if (colorsJson.isNotBlank()) {
            try {
                Json.decodeFromString(colorsJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        },
        pricing = CardPricing(
            markets = listOf(
                MarketPrice(
                    marketName = "TCGPlayer",
                    marketId = MarketPrice.MARKET_TCGPLAYER,
                    normalPrice = price,
                    foilPrice = null,
                    purchaseUrl = null
                )
            )
        )
    ),
    quantity = quantity,
    condition = CardCondition.valueOf(condition),
    addedDate = addedDate,
    notes = notes
)
