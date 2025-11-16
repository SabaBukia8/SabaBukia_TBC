package com.example.sababukia_tbc.data.remote.dto

data class UsersResponseDTO(
    val page: Int,
    val per_page: Int,
    val total: Int,
    val total_pages: Int,
    val data: List<UserDTO>
)
