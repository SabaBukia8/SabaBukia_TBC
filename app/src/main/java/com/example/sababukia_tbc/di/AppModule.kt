package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.repository.BiometricAuthRepositoryImpl
import com.example.sababukia_tbc.domain.repository.BiometricAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindBiometricAuthRepository(
        biometricAuthRepositoryImpl: BiometricAuthRepositoryImpl
    ): BiometricAuthRepository
}
