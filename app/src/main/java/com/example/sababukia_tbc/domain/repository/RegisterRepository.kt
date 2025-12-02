package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.common.Resource
import com.example.sababukia_tbc.domain.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(email: String, password: String): Flow<Resource<AuthResponse>>
}
