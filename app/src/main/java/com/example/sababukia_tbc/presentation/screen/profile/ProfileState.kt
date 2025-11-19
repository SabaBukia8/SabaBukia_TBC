package com.example.sababukia_tbc.presentation.screen.profile

import com.example.sababukia_tbc.data.common.Resource

data class ProfileState(
    val loader: Resource<String> = Resource.Loading(isLoading = false),
    val userEmail: String = ""
)
