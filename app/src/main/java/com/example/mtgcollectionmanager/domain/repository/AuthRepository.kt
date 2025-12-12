package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<Resource<User>>
    suspend fun register(email: String, password: String): Flow<Resource<User>>
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Boolean
    fun logout()
}
