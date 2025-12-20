package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository responsible for AUTHENTICATION ONLY.
 *
 * Single Responsibility: User authentication (login, register, logout)
 *
 * Does NOT handle:
 * - User profile management → See UserProfileRepository
 * - Password changes → See AccountRepository
 * - Account deletion → See AccountRepository
 */
interface AuthRepository {
    /**
     * Authenticate user with email and password.
     * @return Flow emitting authenticated User or error
     */
    suspend fun login(email: String, password: String): Flow<Resource<User>>

    /**
     * Register a new user with email, password, and nickname.
     * Creates both Firebase Auth user and Firestore profile.
     * @return Flow emitting newly created User or error
     */
    suspend fun register(email: String, password: String, nickname: String): Flow<Resource<User>>

    /**
     * Sign out the current user.
     * Clears authentication session.
     */
    fun logout()

    /**
     * Get currently authenticated user (synchronous).
     * @return User if logged in, null otherwise
     */
    fun getCurrentUser(): User?

    /**
     * Check if a user is currently authenticated (synchronous).
     * @return true if user is logged in, false otherwise
     */
    fun isUserLoggedIn(): Boolean
}
