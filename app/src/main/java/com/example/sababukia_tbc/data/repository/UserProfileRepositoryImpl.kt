package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.UserProfile
import com.example.sababukia_tbc.UserProfiles
import com.example.sababukia_tbc.data.model.local.datastore.ProtoDataStoreManager
import com.example.sababukia_tbc.di.UserProfileDataStore
import com.example.sababukia_tbc.domain.model.UserProfileModel
import com.example.sababukia_tbc.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    @UserProfileDataStore private val userProfileStore: ProtoDataStoreManager<UserProfiles>
) : UserProfileRepository {

    override fun getProfiles(): Flow<List<UserProfileModel>> {
        return userProfileStore.data.map { userProfiles ->
            userProfiles.profilesList.map { profile ->
                UserProfileModel(
                    id = profile.id,
                    firstName = profile.firstName,
                    lastName = profile.lastName,
                    email = profile.email
                )
            }
        }
    }

    override suspend fun saveProfile(firstName: String, lastName: String, email: String) {
        val currentProfiles = userProfileStore.data.first()
        val nextId = currentProfiles.nextId

        val newProfile = UserProfile.newBuilder()
            .setId(nextId)
            .setFirstName(firstName.trim())
            .setLastName(lastName.trim())
            .setEmail(email.trim())
            .build()

        userProfileStore.write { profiles ->
            profiles.toBuilder()
                .addProfiles(newProfile)
                .setNextId(nextId + 1)
                .build()
        }
    }

    override suspend fun deleteProfile(profileId: Long) {
        userProfileStore.write { profiles ->
            val updatedList = profiles.profilesList.filter { it.id != profileId }
            profiles.toBuilder()
                .clearProfiles()
                .addAllProfiles(updatedList)
                .build()
        }
    }

    override suspend fun checkEmailExists(email: String, excludeId: Long?): Boolean {
        val currentProfiles = userProfileStore.data.first()
        return currentProfiles.profilesList.any {
            it.email.equals(email, ignoreCase = true) && it.id != excludeId
        }
    }
}
