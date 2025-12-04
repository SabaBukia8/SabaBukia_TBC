package com.example.sababukia_tbc.presentation.mapper

import com.example.sababukia_tbc.domain.model.UserListItem
import com.example.sababukia_tbc.presentation.model.UserListItemUi

fun UserListItem.toUi(): UserListItemUi = UserListItemUi(
    id = id,
    fullName = fullName,
    email = email,
    activationStatus = activationStatus,
    lastActiveDescription = lastActiveDescription,
    lastActiveEpoch = lastActiveEpoch,
    profileImageUrl = profileImageUrl
)
