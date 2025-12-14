package com.example.mtgcollectionmanager.presentation.mapper

import com.example.mtgcollectionmanager.domain.model.Collection
import com.example.mtgcollectionmanager.presentation.model.CollectionUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Collection.toUi(): CollectionUi {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return CollectionUi(
        id = id,
        name = name,
        description = description,
        totalCards = totalCards,
        totalValue = if (totalValue > 0) "$${String.format("%.2f", totalValue)}" else "$0.00",
        createdDate = dateFormat.format(Date(createdDate)),
        userId = userId,
        createdDateMillis = createdDate
    )
}

fun CollectionUi.toDomain(): Collection {
    return Collection(
        id = id,
        name = name,
        description = description,
        createdDate = createdDateMillis,
        userId = userId,
        totalCards = totalCards,
        totalValue = totalValue.removePrefix("$").toDoubleOrNull() ?: 0.0
    )
}
