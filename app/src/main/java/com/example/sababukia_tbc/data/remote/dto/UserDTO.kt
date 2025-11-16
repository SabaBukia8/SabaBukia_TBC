package com.example.sababukia_tbc.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDTO(
    val id: Int,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val avatar: String
)
