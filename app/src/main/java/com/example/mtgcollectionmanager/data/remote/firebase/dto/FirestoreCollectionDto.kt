package com.example.mtgcollectionmanager.data.remote.firebase.dto

import com.example.mtgcollectionmanager.data.model.local.CollectionEntity

data class FirestoreCollectionDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", System.currentTimeMillis())

    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "description" to description,
        "createdAt" to createdAt
    )

    fun toEntity(userId: String): CollectionEntity = CollectionEntity(
        id = id.hashCode().toLong().let { if (it < 0) -it else it },
        name = name,
        description = description,
        createdDate = createdAt,
        userId = userId
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): FirestoreCollectionDto =
            FirestoreCollectionDto(
                id = id,
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )

        fun fromEntity(entity: CollectionEntity): FirestoreCollectionDto = FirestoreCollectionDto(
            id = entity.id.toString(),
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdDate
        )
    }
}
