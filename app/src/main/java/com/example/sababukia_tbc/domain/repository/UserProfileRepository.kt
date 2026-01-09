package com.example.sababukia_tbc.domain.repository

import com.example.sababukia_tbc.domain.model.UserProfileModel
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getProfiles(): Flow<List<UserProfileModel>>
    suspend fun saveProfile(firstName: String, lastName: String, email: String)
    suspend fun deleteProfile(profileId: Long)
    suspend fun checkEmailExists(email: String, excludeId: Long? = null): Boolean
}
