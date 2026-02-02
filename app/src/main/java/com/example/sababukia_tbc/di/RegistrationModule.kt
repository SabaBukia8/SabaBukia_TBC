package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.remote.api.RegistrationApiService
import com.example.sababukia_tbc.data.repository.RegistrationRepositoryImpl
import com.example.sababukia_tbc.domain.repository.RegistrationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegistrationModule {

    @Binds
    @Singleton
    abstract fun bindRegistrationRepository(
        impl: RegistrationRepositoryImpl
    ): RegistrationRepository

    companion object {
        @Provides
        @Singleton
        fun provideRegistrationApiService(retrofit: Retrofit): RegistrationApiService {
            return retrofit.create(RegistrationApiService::class.java)
        }
    }
}
