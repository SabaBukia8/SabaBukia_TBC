package com.example.sababukia_tbc.di

import android.util.Log
import com.example.sababukia_tbc.data.remote.network.MessengerApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TAG = "NetworkModule"

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            prettyPrint = false
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideMessengerApiService(json: Json): MessengerApiService {
        val contentType = "application/json".toMediaType()

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val messengerOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        val messengerRetrofit = Retrofit.Builder()
            .baseUrl("https://mocki.io/v1/")
            .client(messengerOkHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        return messengerRetrofit.create(MessengerApiService::class.java)
    }
}
