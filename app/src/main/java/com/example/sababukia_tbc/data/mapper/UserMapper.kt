package com.example.sababukia_tbc.data.mapper

import com.example.sababukia_tbc.data.model.local.UserEntity
import com.example.sababukia_tbc.data.model.remote.UserDto
import com.example.sababukia_tbc.domain.model.UserListItem

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    fullName = fullName,
    email = email,
    activationStatus = activationStatus,
    lastActiveDescription = lastActiveDescription,
    lastActiveEpoch = lastActiveEpoch,
    profileImageUrl = profileImageUrl
)

fun UserDto.toDomain(): UserListItem = UserListItem(
    id = id,
    fullName = fullName,
    email = email,
    activationStatus = activationStatus,
    lastActiveDescription = lastActiveDescription,
    lastActiveEpoch = lastActiveEpoch,
    profileImageUrl = profileImageUrl
)

fun UserEntity.toDomain(): UserListItem = UserListItem(
    id = id,
    fullName = fullName,
    email = email,
    activationStatus = activationStatus,
    lastActiveDescription = lastActiveDescription,
    lastActiveEpoch = lastActiveEpoch,
    profileImageUrl = profileImageUrl
)
