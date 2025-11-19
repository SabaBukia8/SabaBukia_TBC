package com.example.sababukia_tbc.presentation.screen.home

import com.example.sababukia_tbc.data.common.Resource
import com.example.sababukia_tbc.domain.model.User

data class HomeState(
    val loader: Resource<List<User>> = Resource.Loading(isLoading = false)
)
