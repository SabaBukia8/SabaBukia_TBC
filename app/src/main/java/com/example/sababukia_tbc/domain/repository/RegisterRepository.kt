package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.User

interface RegisterRepository {
    suspend fun register(email: String, password: String): AuthResult<User>
    suspend fun updateDisplayName(nickname: String): AuthResult<Unit>
}
