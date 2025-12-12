package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading(true))
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Login failed"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override suspend fun register(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading(true))
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                emit(Resource.Success(firebaseUser.toUser()))
            } ?: emit(Resource.Error("Registration failed"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        } finally {
            emit(Resource.Loading(false))
        }
    }

    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toUser()

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun logout() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toUser() = User(
        uid = uid,
        email = email ?: "",
        displayName = displayName ?: email?.substringBefore("@") ?: "User"
    )
}
