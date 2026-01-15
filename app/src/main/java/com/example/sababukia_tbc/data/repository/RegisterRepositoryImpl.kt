package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : RegisterRepository {

    override suspend fun register(email: String, password: String): AuthResult<User> =
        withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let {
                    AuthResult.Success(it.toDomain())
                } ?: AuthResult.Error("Registration failed: user is null")
            } catch (e: FirebaseAuthUserCollisionException) {
                AuthResult.Error("Email already used!")
            } catch (e: Exception) {
                AuthResult.Error("Authentication failed: ${e.message}")
            }
        }

    override suspend fun updateDisplayName(nickname: String): AuthResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val user = firebaseAuth.currentUser
                    ?: return@withContext AuthResult.Error("User not signed in")
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()
                user.updateProfile(profileUpdates).await()
                AuthResult.Success(Unit)
            } catch (e: Exception) {
                AuthResult.Error("Couldn't set nickname: ${e.message}")
            }
        }
}
