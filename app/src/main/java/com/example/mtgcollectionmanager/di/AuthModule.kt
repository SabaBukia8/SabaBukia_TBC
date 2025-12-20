package com.example.mtgcollectionmanager.di

import com.example.mtgcollectionmanager.data.repository.AccountRepositoryImpl
import com.example.mtgcollectionmanager.data.repository.AuthRepositoryImpl
import com.example.mtgcollectionmanager.data.repository.UserProfileRepositoryImpl
import com.example.mtgcollectionmanager.domain.repository.AccountRepository
import com.example.mtgcollectionmanager.domain.repository.AuthRepository
import com.example.mtgcollectionmanager.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}


@Module
@InstallIn(SingletonComponent::class)
interface AuthBindsModule {

    @Binds
    @Singleton
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}
