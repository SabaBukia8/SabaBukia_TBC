package com.example.sababukia_tbc.di

import android.util.Log
import com.example.sababukia_tbc.data.DatastoreManager
import com.example.sababukia_tbc.data.model.remote.network.AuthInterceptor
import com.example.sababukia_tbc.data.model.remote.network.LoginApiService
import com.example.sababukia_tbc.data.model.remote.network.RegisterApiService
import com.example.sababukia_tbc.data.model.remote.network.UsersApiService
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

    private const val BASE_URL = "https://reqres.in/"
    private const val TAG = "NetworkModule"
    private const val API_KEY = "reqres-free-v1"

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
    fun provideAuthInterceptor(datastoreManager: DatastoreManager): AuthInterceptor {
        return AuthInterceptor(datastoreManager)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("x-api-key", API_KEY)
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
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginApiService(retrofit: Retrofit): LoginApiService {
        return retrofit.create(LoginApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterApiService(retrofit: Retrofit): RegisterApiService {
        return retrofit.create(RegisterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUsersApiService(retrofit: Retrofit): UsersApiService {
        return retrofit.create(UsersApiService::class.java)
    }
}
