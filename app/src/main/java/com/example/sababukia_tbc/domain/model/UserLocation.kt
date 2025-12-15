package com.example.sababukia_tbc.domain.model

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val provider: String? = null,
    val time: Long? = null
)