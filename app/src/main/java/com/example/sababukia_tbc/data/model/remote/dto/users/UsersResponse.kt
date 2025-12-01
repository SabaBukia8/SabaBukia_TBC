package com.example.sababukia_tbc.data.model.remote.dto.users

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    @SerialName("page")
    val page: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("total")
    val total: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("data")
    val data: List<UserDTO>
) {
    @Serializable
    data class UserDTO(
        @SerialName("id")
        val id: Int,
        @SerialName("email")
        val email: String,
        @SerialName("first_name")
        val firstName: String,
        @SerialName("last_name")
        val lastName: String,
        @SerialName("avatar")
        val avatar: String
    )
}
