package com.example.mtgcollectionmanager.data.mapper

import com.example.mtgcollectionmanager.data.model.local.CollectionCardEntity
import com.example.mtgcollectionmanager.data.model.remote.CardDto
import com.example.mtgcollectionmanager.domain.model.Card
import com.example.mtgcollectionmanager.domain.model.CardCondition
import com.example.mtgcollectionmanager.domain.model.CollectionCard
import kotlinx.serialization.encodeToString
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
    price = prices?.usd?.toDoubleOrNull() ?: 0.0
)

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
    price = price,
    quantity = quantity,
    condition = condition.name,
    addedDate = addedDate,
    notes = notes,
    userId = userId
)

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
        colors = Json.decodeFromString(colorsJson),
        price = price
    ),
    quantity = quantity,
    condition = CardCondition.valueOf(condition),
    addedDate = addedDate,
    notes = notes
)
