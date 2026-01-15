package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.repository.LoginRepositoryImpl
import com.example.sababukia_tbc.data.repository.RegisterRepositoryImpl
import com.example.sababukia_tbc.data.repository.SessionRepositoryImpl
import com.example.sababukia_tbc.domain.repository.LoginRepository
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import com.example.sababukia_tbc.domain.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseAuth: FirebaseAuth
    ): LoginRepository = LoginRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideRegisterRepository(
        firebaseAuth: FirebaseAuth
    ): RegisterRepository = RegisterRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideSessionRepository(
        firebaseAuth: FirebaseAuth
    ): SessionRepository = SessionRepositoryImpl(firebaseAuth)
}
