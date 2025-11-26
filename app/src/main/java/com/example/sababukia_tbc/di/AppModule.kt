package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.repository.AuthRepositoryImpl
import com.example.sababukia_tbc.data.repository.UsersRepositoryImpl
import com.example.sababukia_tbc.domain.repository.AuthRepository
import com.example.sababukia_tbc.domain.repository.UsersRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUsersRepository(
        usersRepositoryImpl: UsersRepositoryImpl
    ): UsersRepository
}
