package com.example.sababukia_tbc.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Registration : Routes
}
