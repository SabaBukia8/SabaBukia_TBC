package com.example.mtgcollectionmanager.data.remote.firebase.dto

data class FirestoreUserProfileDto(
    val nickname: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", System.currentTimeMillis())
//kotlin date time
    fun toMap(): Map<String, Any> = mapOf(
        "nickname" to nickname,
        "email" to email,
        "createdAt" to createdAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): FirestoreUserProfileDto = FirestoreUserProfileDto(
            nickname = map["nickname"] as? String ?: "",
            email = map["email"] as? String ?: "",
            createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
        )
    }
}
