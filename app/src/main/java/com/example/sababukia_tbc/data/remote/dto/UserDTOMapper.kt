package com.example.sababukia_tbc.data.remote.dto

import com.example.sababukia_tbc.domain.model.User

fun UserDTO.toDomain(): User = User(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    avatar = avatar
)

fun List<UserDTO>.toDomain(): List<User> = map { it.toDomain() }
