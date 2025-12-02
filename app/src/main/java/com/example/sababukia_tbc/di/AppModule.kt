package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.repository.LoginRepositoryImpl
import com.example.sababukia_tbc.data.repository.RegisterRepositoryImpl
import com.example.sababukia_tbc.data.repository.UserPreferencesRepositoryImpl
import com.example.sababukia_tbc.data.repository.UsersRepositoryImpl
import com.example.sababukia_tbc.domain.repository.LoginRepository
import com.example.sababukia_tbc.domain.repository.RegisterRepository
import com.example.sababukia_tbc.domain.repository.UserPreferencesRepository
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
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        registerRepositoryImpl: RegisterRepositoryImpl
    ): RegisterRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindUsersRepository(
        usersRepositoryImpl: UsersRepositoryImpl
    ): UsersRepository
}
