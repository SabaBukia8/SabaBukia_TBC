package com.example.mtgcollectionmanager.data.repository

import com.example.mtgcollectionmanager.data.common.resourceFlow
import com.example.mtgcollectionmanager.data.local.dao.CollectionDao
import com.example.mtgcollectionmanager.data.remote.firebase.FirestoreDataSource
import com.example.mtgcollectionmanager.domain.common.AppError
import com.example.mtgcollectionmanager.domain.common.Resource
import com.example.mtgcollectionmanager.domain.repository.AccountRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestoreDataSource: FirestoreDataSource,
    private val collectionDao: CollectionDao
) : AccountRepository {

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> = resourceFlow {
        try {
            val user = firebaseAuth.currentUser
            val email = user?.email

            if (user == null || email == null) {
                emit(Resource.Error(AppError.Auth.UserNotLoggedIn))
                return@resourceFlow
            }

            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            user.updatePassword(newPassword).await()

            emit(Resource.Success(Unit))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(AppError.Auth.InvalidCredentials))
        } catch (_: FirebaseAuthWeakPasswordException) {
            emit(Resource.Error(AppError.Auth.WeakPassword))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Auth.PasswordChangeFailed))
        }
    }

    override suspend fun deleteAccount(password: String): Flow<Resource<Unit>> = resourceFlow {
        try {
            val user = firebaseAuth.currentUser
            val email = user?.email

            if (user == null || email == null) {
                emit(Resource.Error(AppError.Auth.UserNotLoggedIn))
                return@resourceFlow
            }

            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()

            firestoreDataSource.deleteUserData(user.uid)

            collectionDao.deleteAllCollectionsForUser(user.uid)

            user.delete().await()

            emit(Resource.Success(Unit))
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(AppError.Auth.InvalidCredentials))
        } catch (_: Exception) {
            emit(Resource.Error(AppError.Profile.DeleteFailed))
        }
    }
}
