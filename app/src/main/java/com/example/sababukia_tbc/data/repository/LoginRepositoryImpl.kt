package com.example.sababukia_tbc.data.repository

import com.example.sababukia_tbc.data.mapper.toDomain
import com.example.sababukia_tbc.data.util.mapAuthException
import com.example.sababukia_tbc.data.common.safeCallNullable
import com.example.sababukia_tbc.domain.model.AuthError
import com.example.sababukia_tbc.domain.model.AuthResult
import com.example.sababukia_tbc.domain.model.User
import com.example.sababukia_tbc.domain.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRepository {

    override suspend fun login(email: String, password: String): AuthResult<User> =
        safeCallNullable(
            nullError = AuthError.UserIsNull,
            exceptionMapper = ::mapAuthException
        ) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .await()
                .user
                ?.toDomain()
        }
}
