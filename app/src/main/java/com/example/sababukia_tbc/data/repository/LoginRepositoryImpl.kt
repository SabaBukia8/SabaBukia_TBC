package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    override suspend fun login(email: String, password: String): AuthResult<User> =
        withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                result.user?.let {
                    AuthResult.Success(it.toDomain())
                } ?: AuthResult.Error("Login failed: user is null")
            } catch (e: FirebaseAuthInvalidUserException) {
                AuthResult.Error("Couldn't find a user with this email")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                AuthResult.Error("Invalid password. Please try again.")
            } catch (e: Exception) {
                AuthResult.Error("Authentication failed: ${e.message}")
            }
        }
}
