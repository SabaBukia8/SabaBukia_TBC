package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.util.mapAuthException
import com.example.sababukia_tbc.data.util.mapProfileException
import com.example.sababukia_tbc.data.common.safeCall
import com.example.sababukia_tbc.data.common.safeCallNullable
import com.example.sababukia_tbc.domain.model.AuthError
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.Result
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : RegisterRepository {

    override suspend fun register(email: String, password: String): AuthResult<User> =
        safeCallNullable(
            nullError = AuthError.UserIsNull,
            exceptionMapper = ::mapAuthException
        ) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .await()
                .user
                ?.toDomain()    
        }

    override suspend fun updateDisplayName(nickname: String): AuthResult<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.Error(AuthError.UserNotSignedIn)

        return safeCall(exceptionMapper = ::mapProfileException) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build()
            user.updateProfile(profileUpdates).await()
        }
    }
}
