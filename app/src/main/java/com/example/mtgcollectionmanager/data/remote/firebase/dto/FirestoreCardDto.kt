package com.example.mtgcollectionmanager.data.remote.firebase.dto

import com.example.mtgcollectionmanager.data.model.local.CollectionCardEntity

data class FirestoreCardDto(
    val id: String = "",
    val cardId: String = "",
    val name: String = "",
    val manaCost: String = "",
    val imageUrl: String = "",
    val type: String = "",
    val rarity: String = "",
    val setCode: String = "",
    val setName: String = "",
    val colorsJson: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val condition: String = "NEAR_MINT",
    val addedDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val categoryId: String? = null
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        0.0,
        1,
        "NEAR_MINT",
        System.currentTimeMillis(),
        "",
        null
    )

    fun toMap(): Map<String, Any?> = mapOf(
        "cardId" to cardId,
        "name" to name,
        "manaCost" to manaCost,
        "imageUrl" to imageUrl,
        "type" to type,
        "rarity" to rarity,
        "setCode" to setCode,
        "setName" to setName,
        "colorsJson" to colorsJson,
        "price" to price,
        "quantity" to quantity,
        "condition" to condition,
        "addedDate" to addedDate,
        "notes" to notes,
        "categoryId" to categoryId
    )

    fun toEntity(collectionId: Long, userId: String): CollectionCardEntity = CollectionCardEntity(
        id = id.hashCode().toLong().let { if (it < 0) -it else it },
        collectionId = collectionId,
        categoryId = categoryId?.hashCode()?.toLong()?.let { if (it < 0) -it else it },
        cardId = cardId,
        name = name,
        manaCost = manaCost,
        imageUrl = imageUrl,
        type = type,
        rarity = rarity,
        setCode = setCode,
        setName = setName,
        colorsJson = colorsJson,
        price = price,
        quantity = quantity,
        condition = condition,
        addedDate = addedDate,
        notes = notes,
        userId = userId
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): FirestoreCardDto = FirestoreCardDto(
            id = id,
            cardId = map["cardId"] as? String ?: "",
            name = map["name"] as? String ?: "",
            manaCost = map["manaCost"] as? String ?: "",
            imageUrl = map["imageUrl"] as? String ?: "",
            type = map["type"] as? String ?: "",
            rarity = map["rarity"] as? String ?: "",
            setCode = map["setCode"] as? String ?: "",
            setName = map["setName"] as? String ?: "",
            colorsJson = map["colorsJson"] as? String ?: "",
            price = (map["price"] as? Number)?.toDouble() ?: 0.0,
            quantity = (map["quantity"] as? Number)?.toInt() ?: 1,
            condition = map["condition"] as? String ?: "NEAR_MINT",
            addedDate = map["addedDate"] as? Long ?: System.currentTimeMillis(),
            notes = map["notes"] as? String ?: "",
            categoryId = map["categoryId"] as? String
        )

        fun fromEntity(entity: CollectionCardEntity): FirestoreCardDto = FirestoreCardDto(
            id = entity.id.toString(),
            cardId = entity.cardId,
            name = entity.name,
            manaCost = entity.manaCost,
            imageUrl = entity.imageUrl,
            type = entity.type,
            rarity = entity.rarity,
            setCode = entity.setCode,
            setName = entity.setName,
            colorsJson = entity.colorsJson,
            price = entity.price,
            quantity = entity.quantity,
            condition = entity.condition,
            addedDate = entity.addedDate,
            notes = entity.notes,
            categoryId = entity.categoryId?.toString()
        )
    }
}
