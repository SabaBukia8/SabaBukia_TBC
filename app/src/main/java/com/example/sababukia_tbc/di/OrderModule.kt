package com.example.sababukia_tbc.di

import com.example.sababukia_tbc.data.remote.OrderApi
import com.example.sababukia_tbc.data.repository.OrderRepositoryImpl
import com.example.sababukia_tbc.domain.repository.OrderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi =
        retrofit.create(OrderApi::class.java)

    @Provides
    @Singleton
    fun provideOrderRepository(api: OrderApi): OrderRepository =
        OrderRepositoryImpl(api)
}
