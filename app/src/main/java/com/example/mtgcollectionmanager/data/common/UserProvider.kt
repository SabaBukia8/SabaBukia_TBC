package com.example.mtgcollectionmanager.data.common

import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserProvider @Inject constructor(
    private val authRepository: AuthRepository
) {

    fun getCurrentUserId(): String = 
        authRepository.getCurrentUser()?.uid ?: ""
}