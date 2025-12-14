package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import com.example.mtgcollectionmanager.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<Resource<User>>
    suspend fun register(email: String, password: String, nickname: String): Flow<Resource<User>>
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Boolean
    fun logout()

    suspend fun getUserProfile(): Flow<Resource<UserProfile>>
    suspend fun updateNickname(nickname: String): Flow<Resource<Unit>>
    suspend fun changePassword(currentPassword: String, newPassword: String): Flow<Resource<Unit>>
    suspend fun deleteAccount(password: String): Flow<Resource<Unit>>
}
