package com.example.sababukia_tbc.di

import android.util.Log
import com.example.sababukia_tbc.BuildConfig
import com.example.sababukia_tbc.data.remote.api.UserListApiService
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserListRetrofit

@Module
@InstallIn(SingletonComponent::class)
object UserListModule {

    private const val TAG = "UserListModule"

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
    @UserListRetrofit
    fun provideUserListOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor { message ->
                    Log.d(TAG, message)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                try {
                    val response = chain.proceed(newRequest)
                    Log.d(TAG, "Response Code: ${response.code}")
                    response
                } catch (e: Exception) {
                    Log.e(TAG, "Network request failed", e)
                    throw e
                }
            }
            .build()
    }

    @Provides
    @Singleton
    @UserListRetrofit
    fun provideUserListRetrofit(
        @UserListRetrofit okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserListApiService(@UserListRetrofit retrofit: Retrofit): UserListApiService {
        return retrofit.create(UserListApiService::class.java)
    }
}
