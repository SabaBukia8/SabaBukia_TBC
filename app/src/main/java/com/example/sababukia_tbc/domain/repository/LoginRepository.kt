package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.User

interface LoginRepository {
    suspend fun login(email: String, password: String): AuthResult<User>
}
