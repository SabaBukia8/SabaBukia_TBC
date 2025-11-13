package di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.DatastoreManager
import network.AuthApiService
import network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")

                if (API_KEY.isNotEmpty()) {
                    requestBuilder.addHeader("x-api-key", API_KEY)
                }

                val newRequest = requestBuilder.build()
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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
