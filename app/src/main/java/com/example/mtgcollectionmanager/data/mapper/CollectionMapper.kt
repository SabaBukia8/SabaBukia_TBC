package com.example.mtgcollectionmanager.data.mapper

import com.example.mtgcollectionmanager.data.model.local.CollectionEntity
import com.example.mtgcollectionmanager.domain.model.Collection
//default argumentebi mappershi
fun CollectionEntity.toDomain(
    totalCards: Int = 0,
    totalValue: Double = 0.0
): Collection = Collection(
    id = id,
    name = name,
    description = description,
    createdDate = createdDate,
    userId = userId,
    totalCards = totalCards,
    totalValue = totalValue,
    firestoreId = firestoreId
)
//dto da entity calke
fun Collection.toEntity(): CollectionEntity = CollectionEntity(
    id = id,
    name = name,
    description = description,
    createdDate = createdDate,
    userId = userId,
    firestoreId = firestoreId
)
