package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.remote.dto.users.UsersResponse
import com.example.sababukia_tbc.domain.model.User

fun UsersResponse.UserDTO.toDomain(): User = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatar = avatar
)

fun List<UsersResponse.UserDTO>.toDomain(): List<User> = map { it.toDomain() }
