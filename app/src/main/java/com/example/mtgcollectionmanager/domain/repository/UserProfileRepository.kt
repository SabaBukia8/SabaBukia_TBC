package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository responsible for USER PROFILE MANAGEMENT.
 *
 * Single Responsibility: Managing user profile data
 *
 * Handles:
 * - Loading user profile with stats
 * - Updating user nickname
 *
 * Does NOT handle:
 * - Authentication → See AuthRepository
 * - Password changes → See AccountRepository
 * - Account deletion → See AccountRepository
 */
interface UserProfileRepository {
    /**
     * Load the current user's profile with statistics.
     * Syncs collections and cards from Firestore.
     * @return Flow emitting UserProfile with collection/card counts or error
     */
    suspend fun getUserProfile(): Flow<Resource<UserProfile>>

    /**
     * Update the current user's nickname.
     * @param nickname New nickname to set
     * @return Flow emitting success or error
     */
    suspend fun updateNickname(nickname: String): Flow<Resource<Unit>>
}
