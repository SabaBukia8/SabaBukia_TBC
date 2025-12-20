package com.example.mtgcollectionmanager.domain.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository responsible for ACCOUNT MANAGEMENT (sensitive operations).
 *
 * Single Responsibility: Managing account-level operations that require re-authentication
 *
 * Handles:
 * - Changing password (requires current password verification)
 * - Deleting account (requires password verification + data cleanup)
 *
 * Does NOT handle:
 * - Initial authentication → See AuthRepository
 * - Profile updates (nickname, etc.) → See UserProfileRepository
 */
interface AccountRepository {
    /**
     * Change the current user's password.
     * Requires re-authentication with current password for security.
     *
     * @param currentPassword User's current password for verification
     * @param newPassword New password to set
     * @return Flow emitting success or error (InvalidCredentials, WeakPassword, etc.)
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>>

    /**
     * Permanently delete the current user's account.
     * Requires password verification for security.
     *
     * This operation:
     * 1. Re-authenticates the user
     * 2. Deletes all user data from Firestore
     * 3. Deletes all local collections
     * 4. Deletes the Firebase Auth account
     *
     * @param password User's password for verification
     * @return Flow emitting success or error
     */
    suspend fun deleteAccount(password: String): Flow<Resource<Unit>>
}
