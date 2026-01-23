package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.remote.ChatApi
import com.example.sababukia_tbc.data.repository.ChatRepositoryImpl
import com.example.sababukia_tbc.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi =
        retrofit.create(ChatApi::class.java)

    @Provides
    @Singleton
    fun provideChatRepository(api: ChatApi): ChatRepository =
        ChatRepositoryImpl(api)
}
