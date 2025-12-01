package com.example.sababukia_tbc.presentation.screen.users.mapper

import androidx.paging.PagingData
import androidx.paging.map
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.presentation.screen.users.model.UserUiModel

fun User.toUiModel(): UserUiModel = UserUiModel(
    id = id,
    fullName = fullName,
    email = email,
    avatar = avatar
)

fun PagingData<User>.toUiModel(): PagingData<UserUiModel> = map { it.toUiModel() }
