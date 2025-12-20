package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.data.remote.firebase.dto.FirestoreUserProfileDto
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.model.User
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDataSource: FirestoreDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Flow<Resource<User>> =
        resourceFlow {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    emit(Resource.Success(firebaseUser.toUser()))
                } ?: emit(Resource.Error(AppError.Auth.LoginFailed))
            } catch (_: FirebaseAuthInvalidCredentialsException) {
                emit(Resource.Error(AppError.Auth.InvalidCredentials))
            } catch (_: FirebaseAuthInvalidUserException) {
                emit(Resource.Error(AppError.Auth.InvalidCredentials))
            } catch (e: Exception) {
                emit(Resource.Error(AppError.Unknown(e.message)))
            }
        }

    override suspend fun register(
        email: String,
        password: String,
        nickname: String
    ): Flow<Resource<User>> = resourceFlow {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            result.user?.let { firebaseUser ->
                val profile = FirestoreUserProfileDto(
                    nickname = nickname.ifBlank { email.substringBefore("@") },
                    email = email,
                    createdAt = System.currentTimeMillis()
                )
                firestoreDataSource.createUserProfile(firebaseUser.uid, profile)

                emit(Resource.Success(firebaseUser.toUser(nickname)))
            } ?: emit(Resource.Error(AppError.Auth.RegistrationFailed))
        } catch (_: FirebaseAuthUserCollisionException) {
            emit(Resource.Error(AppError.Auth.EmailAlreadyInUse))
        } catch (_: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error(AppError.Auth.WeakPassword))
        } catch (e: Exception) {
            emit(Resource.Error(AppError.Unknown(e.message)))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toUser()

    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null


    private fun FirebaseUser.toUser(nickname: String? = null) = User(
        uid = uid,
        email = email ?: "",
        displayName = nickname ?: displayName ?: email?.substringBefore("@") ?: "User"
    )

}
