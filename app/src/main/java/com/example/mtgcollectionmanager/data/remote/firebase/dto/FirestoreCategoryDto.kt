package com.example.mtgcollectionmanager.data.remote.firebase.dto

import com.example.mtgcollectionmanager.data.model.local.CategoryEntity

data class FirestoreCategoryDto(
    val id: String = "",
    val name: String = "",
    val color: String = "#808080",
    val createdDate: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "#808080", System.currentTimeMillis())

    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "color" to color,
        "createdDate" to createdDate
    )

    fun toEntity(collectionId: Long): CategoryEntity = CategoryEntity(
        id = id.hashCode().toLong().let { if (it < 0) -it else it },
        collectionId = collectionId,
        name = name,
        color = color,
        createdDate = createdDate
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): FirestoreCategoryDto =
            FirestoreCategoryDto(
                id = id,
                name = map["name"] as? String ?: "",
                color = map["color"] as? String ?: "#808080",
                createdDate = map["createdDate"] as? Long ?: System.currentTimeMillis()
            )

        fun fromEntity(entity: CategoryEntity): FirestoreCategoryDto = FirestoreCategoryDto(
            id = entity.id.toString(),
            name = entity.name,
            color = entity.color,
            createdDate = entity.createdDate
        )
    }
}
