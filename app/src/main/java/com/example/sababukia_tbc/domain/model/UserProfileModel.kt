package com.example.sababukia_tbc.domain.model

data class UserProfileModel(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
) {
    val fullName: String
        get() = "$firstName $lastName"
}
